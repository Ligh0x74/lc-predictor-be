package com.example.lcpredictor.task;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.utils.crawler.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * 竞赛数据抓取任务
 */
@Component
public class CrawlerTask {

    @Autowired
    private LcUserService lcUserService;

    @Autowired
    private LcContestService lcContestService;

    @Autowired
    private LcPredictService lcPredictService;

    @Autowired
    private PredictTask predictTask;

    /**
     * 定时任务, 抓取竞赛数据, 将数据存储到数据库中
     *
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    @Scheduled(cron = "0 15 0,12 * * 0", zone = "Asia/Shanghai")
    public void execute() throws InterruptedException {
        String contestName = process(Requests.request());
        // 如果竞赛信息不在数据库中, 则执行任务
        if (contestName != null) {
            execute(contestName);
        }
    }

    /**
     * 抓取竞赛数据, 将数据存储到数据库中
     * 由于网络故障, 可能需要手动执行特定竞赛名称的任务, 所以抽出代码作为函数
     *
     * @param contestName 竞赛名称
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    public void execute(String contestName) throws InterruptedException {
        Integer contestId = Common.parseContestName(contestName);
        int total = JSONUtil.parseObj(Requests.request(contestName, 1)).getInt("user_num");
        int count = (total + Common.USER_PER_PAGE - 1) / Common.USER_PER_PAGE;
        for (int i = 1; i <= count; i++) {
            if (!process(contestId, Requests.request(contestName, i))) {
                break;
            }
        }
        predictTask.execute(contestName);
    }

    /**
     * 处理竞赛信息, 将其存储到数据库中
     *
     * @param json 竞赛信息 JSON
     * @return 如果竞赛信息不存在于数据库中, 则返回竞赛名称, 否则返回 null.
     */
    private String process(String json) {
        JSONObject jsonObject = JSONUtil.parseObj(json)
                .getByPath("data.contestHistory.contests[0]", JSONObject.class);
        String contestName = jsonObject.getStr("titleSlug");
        Integer startTime = jsonObject.getInt("startTime");
        LcContest contest = new LcContest();
        contest.setContestId(Common.parseContestName(contestName));
        contest.setStartTime(LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.ofHours(8)));
        boolean exists = lcContestService.exists(new LambdaQueryWrapper<LcContest>()
                .eq(LcContest::getContestId, contest.getContestId()));
        if (exists) {
            return null;
        }
        lcContestService.save(contest);
        return contestName;
    }

    /**
     * 处理竞赛页面 JSON, 抓取页面中的每个用户数据, 将数据存储到数据库中
     *
     * @param contestId 竞赛编号
     * @param json      竞赛页面 JSON
     * @return true 继续抓取竞赛页面, false 结束任务
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    private boolean process(Integer contestId, String json) throws InterruptedException {
        JSONArray totalRank = JSONUtil.parseObj(json).getJSONArray("total_rank");
        JSONArray submissions = JSONUtil.parseObj(json).getJSONArray("submissions");
        for (int i = 0; i < totalRank.size(); i++) {
            // 如果用户参赛但没有成功的提交, 则停止抓取之后的竞赛页面
            if (submissions.get(i, JSONObject.class).isEmpty()) {
                return false;
            }
            JSONObject jsonObject = (JSONObject) totalRank.get(i);
            String dataRegion = jsonObject.getStr("data_region");
            String username = jsonObject.getStr("user_slug");
            String nickname = jsonObject.getStr("real_name");
            String avatar = jsonObject.getStr("avatar_url");
            Integer rank = jsonObject.getInt("rank_v2");

            // 如果预测表中存在竞赛编号 & 数据区域 & 用户名, 则不需要重复抓取数据
            // 应对场景: 由于网络故障, 可能会重新执行处理到一半的任务
            boolean exists = lcPredictService.exists(new LambdaQueryWrapper<LcPredict>()
                    .eq(LcPredict::getContestId, contestId)
                    .eq(LcPredict::getDataRegion, dataRegion)
                    .eq(LcPredict::getUsername, username));
            if (exists) {
                continue;
            }

            LcUser user = new LcUser();
            user.setDataRegion(dataRegion);
            user.setUsername(username);
            user.setNickname(nickname);
            user.setAvatar(avatar);
            updateOrInsert(user);

            LcPredict predict = new LcPredict();
            predict.setContestId(contestId);
            predict.setDataRegion(dataRegion);
            predict.setUsername(username);
            predict.setRanking(rank);

            // 双赛周, 周赛的数据需要基于双周赛计算
            // 如果当前是周赛 (竞赛编号为奇数), 且周赛场次是奇数 (周赛编号 = 周赛场次 * 2 + 1), 则当前是双赛周
            if (contestId % 2 == 1 && (contestId - 1) / 2 % 2 == 1) {
                // 双周赛场次 = (周赛场次 - 137) / 2, 双周赛编号 = 双周赛场次 * 2
                LcPredict pre = lcPredictService.lambdaQuery()
                        .eq(LcPredict::getContestId, (contestId - 1) / 2 - 137)
                        .eq(LcPredict::getDataRegion, dataRegion)
                        .eq(LcPredict::getUsername, username)
                        .one();
                // 如果该用户有参加该双周赛, 则从数据库中获取数据做处理, 否则依然发送请求获取数据
                if (pre != null) {
                    predict.setAttendedCount(pre.getAttendedCount() + 1);
                    predict.setOldRating(pre.getNewRating());
                    lcPredictService.save(predict);
                    continue;
                }
            }
            jsonObject = JSONUtil.parseObj(Requests.request(dataRegion, username))
                    .getByPath("data.userContestRanking", JSONObject.class);
            if (jsonObject == null) {
                predict.setAttendedCount(0);
                predict.setOldRating(1500.0);
            } else {
                predict.setAttendedCount(jsonObject.getInt("attendedContestsCount"));
                predict.setOldRating(jsonObject.getDouble("rating"));
            }
            lcPredictService.save(predict);
        }
        return true;
    }

    /**
     * 根据 username & dataRegion 是否存在, 更新/插入数据
     *
     * @param user 用户对象
     */
    private void updateOrInsert(LcUser user) {
        boolean ok = lcUserService.lambdaUpdate()
                .eq(LcUser::getDataRegion, user.getDataRegion())
                .eq(LcUser::getUsername, user.getUsername())
                .update(user);
        if (!ok) {
            lcUserService.save(user);
        }
    }
}

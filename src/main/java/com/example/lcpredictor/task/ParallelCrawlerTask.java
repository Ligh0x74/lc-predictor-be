package com.example.lcpredictor.task;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.utils.crawler.Requests;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 并行竞赛数据抓取任务
 */
@Component
public class ParallelCrawlerTask extends CrawlerTask {

    @Autowired
    private LcPredictService lcPredictService;

    @Autowired
    private PredictTask predictTask;

    /**
     * 抓取竞赛数据, 将数据存储到数据库中
     * 由于网络故障, 可能需要手动执行特定竞赛名称的任务, 所以抽出代码作为函数
     *
     * @param contestName 竞赛名称
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    @Override
    public void execute(String contestName) throws InterruptedException {
        if (!contestCrawler(contestName) || !userCrawler(contestName)) {
            return;
        }
        predictTask.execute(contestName);
    }

    /**
     * 抓取竞赛数据, 将数据存储到数据库中
     *
     * @param contestName 竞赛名称
     * @return 如果任务完成, 则返回 true, 否则返回 false
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    public boolean contestCrawler(String contestName) throws InterruptedException {
        int total = JSONUtil.parseObj(Requests.request(contestName, 1)).getInt("user_num");
        int count = (total + Common.USER_PER_PAGE - 1) / Common.USER_PER_PAGE;
        ExecutorService executor = Executors.newFixedThreadPool(5);
        for (int i = 1; i <= count; i++) {
            int finalI = i;
            executor.execute(() -> {
                try {
                    process(contestName, Requests.request(contestName, finalI));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
        return shutdownExecutor(executor);
    }

    /**
     * 等待执行器完成所有任务
     *
     * @param executor 执行器
     * @return 如果执行器完成所有任务, 返回 true, 否则返回 false
     */
    private boolean shutdownExecutor(ExecutorService executor) {
        executor.shutdown();
        try {
            if (executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES)) {
                return true;
            }
            executor.shutdownNow();
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
        return false;
    }

    /**
     * 处理竞赛页面 JSON, 将数据存储到数据库中
     *
     * @param contestName 竞赛名称
     * @param json        竞赛页面 JSON
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    private void process(String contestName, String json) throws InterruptedException {
        int contestId = Common.parseContestName(contestName);
        JSONArray totalRank = JSONUtil.parseObj(json).getJSONArray("total_rank");
        JSONArray submissions = JSONUtil.parseObj(json).getJSONArray("submissions");
        for (int i = 0; i < totalRank.size(); i++) {
            // 如果用户参赛但没有成功的提交, 则停止处理之后的数据
            if (submissions.get(i, JSONObject.class).isEmpty()) {
                return;
            }
            JSONObject jsonObject = (JSONObject) totalRank.get(i);
            String dataRegion = jsonObject.getStr("data_region");
            String username = jsonObject.getStr("user_slug");
            String nickname = jsonObject.getStr("real_name");
            String avatar = jsonObject.getStr("avatar_url");
            Integer rank = jsonObject.getInt("rank_v2");

            // 如果预测表中存在竞赛编号 & 数据区域 & 用户名, 则不需要重复处理
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
            lcPredictService.save(predict);
        }
    }

    /**
     * 抓取用户数据, 将数据存储到数据库中
     *
     * @param contestName 竞赛名称
     * @return 如果任务完成, 则返回 true, 否则返回 false
     */
    public boolean userCrawler(String contestName) {
        Integer contestId = Common.parseContestName(contestName);
        List<LcPredict> cnPredictList = lcPredictService.lambdaQuery()
                .eq(LcPredict::getContestId, contestId)
                .eq(LcPredict::getDataRegion, "CN")
                .list();
        List<LcPredict> usPredictList = lcPredictService.lambdaQuery()
                .eq(LcPredict::getContestId, contestId)
                .eq(LcPredict::getDataRegion, "US")
                .list();

        ExecutorService cnExecutor = Executors.newFixedThreadPool(1);
        ExecutorService usExecutor = Executors.newFixedThreadPool(5);
        cnPredictList.forEach(predict -> cnExecutor.execute(() -> userTask(predict)));
        usPredictList.forEach(predict -> usExecutor.execute(() -> userTask(predict)));
        return shutdownExecutor(cnExecutor) && shutdownExecutor(usExecutor);
    }

    private void userTask(LcPredict predict) {
        try {
            process(predict);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 抓取用户信息, 将数据存储到数据库中
     *
     * @param predict 预测对象
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    private void process(LcPredict predict) throws InterruptedException {
        // 如果数据已经存在, 则不需要重复抓取
        // 应对场景: 由于网络故障, 可能会重新执行处理到一半的任务
        if (predict.getOldRating() != null) {
            return;
        }
        Integer contestId = predict.getContestId();
        String dataRegion = predict.getDataRegion();
        String username = predict.getUsername();
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
                update(predict);
                return;
            }
        }
        JSONObject jsonObject = JSONUtil.parseObj(Requests.request(dataRegion, username))
                .getByPath("data.userContestRanking", JSONObject.class);
        if (jsonObject == null) {
            predict.setAttendedCount(0);
            predict.setOldRating(1500.0);
        } else {
            predict.setAttendedCount(jsonObject.getInt("attendedContestsCount"));
            predict.setOldRating(jsonObject.getDouble("rating"));
        }
        update(predict);
    }

    /**
     * 更新预测对象
     *
     * @param predict 预测对象
     */
    private void update(LcPredict predict) {
        lcPredictService.lambdaUpdate()
                .eq(LcPredict::getContestId, predict.getContestId())
                .eq(LcPredict::getDataRegion, predict.getDataRegion())
                .eq(LcPredict::getUsername, predict.getUsername())
                .update(predict);
    }
}

package com.example.lcpredictor.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.utils.RedisKey;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.utils.crawler.PredictorFFT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 预测任务
 */
@Slf4j
@Component
public class PredictTask {

    @Autowired
    private LcPredictService lcPredictService;

    @Autowired
    private LcContestService lcContestService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 根据竞赛名称从数据库获取预测列表, 执行预测算法, 将结果存储到数据库中
     *
     * @param contestName 竞赛名称
     */
    public void execute(String contestName) {
        Integer contestId = Common.parseContestName(contestName);
        List<LcPredict> predictList = lcPredictService.lambdaQuery()
                .eq(LcPredict::getContestId, contestId).list();
        TimeInterval timer = DateUtil.timer();
        timer.start();
        PredictorFFT.execute(predictList);
        log.info("PREDICT ELAPSED Time: " + timer.interval() / 1000.0);
        timer.start();
//        predictList.forEach(predict -> lcPredictService.lambdaUpdate()
//                .eq(LcPredict::getContestId, contestId)
//                .eq(LcPredict::getDataRegion, predict.getDataRegion())
//                .eq(LcPredict::getUsername, predict.getUsername())
//                .update(predict));
//
//        predictList.forEach(predict -> lcPredictService.lambdaUpdate()
//                .eq(LcPredict::getId, predict.getId())
//                .update(predict));

        // 表上无其他索引, 耗时分别为 1050s, 450s, 4s
        // 1050s -> 450s, 主要是使用主键索引查询更快, 不需要全表扫描
        // 450s -> 4s, 通过查看 MySQL 的 General Query Log 日志发现, mybatis-plus 的批量更新操作
        // 将所有更新语句放在一个事务中, 而不是为每条更新语句建立一个事务, 没想到时间差距这么大
        lcPredictService.updateBatchById(predictList);
        log.info("UPDATE ELAPSED Time: " + timer.interval() / 1000.0);

        // 更新预测时间
        LcContest contest = new LcContest();
        contest.setPredictTime(new Date(System.currentTimeMillis()));
        lcContestService.lambdaUpdate().eq(LcContest::getContestId, contestId).update(contest);

        // 更新之后, 需要删除相关缓存, 刷新数据
        // 具体来说, 就是竞赛页面的第一页, 和指定竞赛的所有预测页面
        redisTemplate.delete(RedisKey.contestKey(1));
        try (Cursor<String> cursor = redisTemplate.scan(ScanOptions.scanOptions()
                .match(RedisKey.predictKey(contestName)).count(500).build())) {
            redisTemplate.delete(cursor.stream().toList());
        }
    }
}

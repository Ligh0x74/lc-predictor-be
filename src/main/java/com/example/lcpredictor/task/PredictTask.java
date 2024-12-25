package com.example.lcpredictor.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.utils.crawler.Predictor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 预测任务
 */
@Slf4j
@Component
public class PredictTask {

    @Autowired
    private LcPredictService lcPredictService;

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
        Predictor.predict(predictList);
        log.info("PREDICT ELAPSED Time: " + timer.interval() / 1000.0);
        timer.start();
        predictList.forEach(predict -> lcPredictService.lambdaUpdate()
                .eq(LcPredict::getContestId, contestId)
                .eq(LcPredict::getDataRegion, predict.getDataRegion())
                .eq(LcPredict::getUsername, predict.getUsername())
                .update(predict));
        log.info("UPDATE ELAPSED Time: " + timer.interval() / 1000.0);
    }
}

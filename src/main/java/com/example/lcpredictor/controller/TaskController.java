package com.example.lcpredictor.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.task.ParallelCrawlerTask;
import com.example.lcpredictor.task.PredictTask;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.utils.crawler.Requests;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private LcContestService lcContestService;

    @Autowired
    private ParallelCrawlerTask parallelCrawlerTask;

    @Autowired
    private PredictTask predictTask;

    @PostMapping("/crawl/{contestName}")
    public Result<?> crawl(@PathVariable("contestName") String contestName) throws InterruptedException {
        Integer contestId = Common.parseContestName(contestName);
        boolean exists = lcContestService.lambdaQuery().eq(LcContest::getContestId, contestId).exists();
        if (!exists) {
            JSONObject jsonObject = JSONUtil.parseObj(Requests.request(contestName))
                    .getJSONObject("contest");
            Integer startTime = jsonObject.getInt("start_time");
            LcContest contest = new LcContest();
            contest.setContestId(contestId);
            contest.setStartTime(new Date(startTime));
            lcContestService.save(contest);
        }
        parallelCrawlerTask.execute(contestName);
        return Result.success();
    }

    @PostMapping("/userCrawl/{contestName}")
    public Result<?> userCrawl(@PathVariable("contestName") String contestName) {
        parallelCrawlerTask.userCrawler(contestName);
        predictTask.execute(contestName);
        return Result.success();
    }

    @PostMapping("/predict/{contestName}")
    public Result<?> predict(@PathVariable("contestName") String contestName) {
        predictTask.execute(contestName);
        return Result.success();
    }
}

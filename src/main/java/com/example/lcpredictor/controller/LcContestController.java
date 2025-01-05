package com.example.lcpredictor.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.dto.LcContestDTO;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.task.ParallelCrawlerTask;
import com.example.lcpredictor.utils.crawler.Common;
import com.example.lcpredictor.utils.crawler.Requests;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/contest")
public class LcContestController {

    @Autowired
    private LcContestService lcContestService;

    @Autowired
    ParallelCrawlerTask parallelCrawlerTask;

    @GetMapping("/{pageIndex}/{pageSize}")
    public Result<PageVo<LcContestDTO>> get(@PathVariable("pageIndex") Integer pageIndex,
                                            @PathVariable("pageSize") Integer pageSize) {
        return lcContestService.get(pageIndex, pageSize);
    }

    @PostMapping("/{contestName}")
    public Result<?> crawlerTask(@PathVariable("contestName") String contestName) throws InterruptedException {
        Integer contestId = Common.parseContestName(contestName);
        boolean exists = lcContestService.lambdaQuery().eq(LcContest::getContestId, contestId).exists();
        if (!exists) {
            JSONObject jsonObject = JSONUtil.parseObj(Requests.request(contestName))
                    .getJSONObject("contest");
            Integer startTime = jsonObject.getInt("start_time");
            LcContest contest = new LcContest();
            contest.setContestId(contestId);
            contest.setStartTime(LocalDateTime.ofEpochSecond(startTime, 0, ZoneOffset.ofHours(8)));
            lcContestService.save(contest);
        }
        parallelCrawlerTask.execute(contestName);
        return Result.success();
    }
}

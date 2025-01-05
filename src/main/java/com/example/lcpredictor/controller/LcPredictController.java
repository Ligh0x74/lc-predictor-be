package com.example.lcpredictor.controller;

import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.task.PredictTask;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/predict")
public class LcPredictController {

    @Autowired
    private LcPredictService lcPredictService;

    @Autowired
    private PredictTask predictTask;

    @GetMapping("/{contestName}/{pageIndex}/{pageSize}")
    public Result<PageVo<LcPredictDTO>> get(@PathVariable("contestName") String contestName,
                                            @PathVariable("pageIndex") Integer pageIndex,
                                            @PathVariable("pageSize") Integer pageSize) {
        return lcPredictService.get(contestName, pageIndex, pageSize);
    }

    @GetMapping
    public Result<LcPredictDTO> get(@RequestParam("contestName") String contestName,
                                    @RequestParam("dataRegion") String dataRegion,
                                    @RequestParam("username") String username) {
        return lcPredictService.get(contestName, dataRegion, username);
    }

    @PostMapping("/{contestName}")
    public Result<?> predictTask(@PathVariable("contestName") String contestName) {
        predictTask.execute(contestName);
        return Result.success();
    }
}

package com.example.lcpredictor.controller;

import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/predict")
public class LcPredictController {

    @Autowired
    private LcPredictService lcPredictService;

    @GetMapping("/{contestName}/{pageIndex}/{pageSize}")
    public Result<PageVo<LcPredictDTO>> get(@PathVariable("contestName") String contestName,
                                            @PathVariable("pageIndex") Integer pageIndex,
                                            @PathVariable("pageSize") Integer pageSize) {
        return lcPredictService.get(contestName, pageIndex, pageSize);
    }
}

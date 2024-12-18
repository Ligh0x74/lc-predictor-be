package com.example.lcpredictor.controller;

import com.example.lcpredictor.dto.LcContestDTO;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contest")
public class LcContestController {

    @Autowired
    private LcContestService lcContestService;

    @GetMapping("/{pageIndex}/{pageSize}")
    public Result<PageVo<LcContestDTO>> getContestPage(@PathVariable("pageIndex") Integer pageIndex,
                                                       @PathVariable("pageSize") Integer pageSize) {
        return lcContestService.getContestPage(pageIndex, pageSize);
    }
}

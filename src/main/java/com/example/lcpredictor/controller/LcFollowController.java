package com.example.lcpredictor.controller;

import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.service.LcFollowService;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/follow")
public class LcFollowController {

    @Autowired
    private LcFollowService lcFollowService;

    @PostMapping("/{dataRegion}/{username}/{follow}")
    public Result<?> follow(@PathVariable("dataRegion") String dataRegion,
                            @PathVariable("username") String username,
                            @PathVariable("follow") Boolean isFollow) {
        return lcFollowService.follow(dataRegion, username, isFollow);
    }

    @GetMapping("/{contestName}/{pageIndex}/{pageSize}")
    public Result<PageVo<LcPredictDTO>> get(@PathVariable("contestName") String contestName,
                                            @PathVariable("pageIndex") Integer pageIndex,
                                            @PathVariable("pageSize") Integer pageSize) {
        return lcFollowService.get(contestName, pageIndex, pageSize);
    }
}

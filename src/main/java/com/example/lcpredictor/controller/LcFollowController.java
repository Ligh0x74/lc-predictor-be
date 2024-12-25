package com.example.lcpredictor.controller;

import com.example.lcpredictor.service.LcFollowService;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/follow")
public class LcFollowController {

    @Autowired
    private LcFollowService lcFollowService;

    @PostMapping("/{dataRegion}/{username}/{follow}")
    public Result<?> follow(@PathVariable("dataRegion") String dataRegion,
                            @PathVariable("username") String username,
                            @PathVariable("follow") Boolean follow) {
        return lcFollowService.follow(dataRegion, username, follow);
    }
}

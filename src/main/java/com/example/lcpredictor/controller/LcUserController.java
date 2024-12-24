package com.example.lcpredictor.controller;

import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class LcUserController {

    @Autowired
    LcUserService lcUserService;

    @PostMapping("/login/{dataRegion}/{username}")
    public Result<LcUserDTO> login(@RequestParam("dataRegion") String dataRegion,
                                   @RequestParam("username") String username) throws InterruptedException {
        return lcUserService.login(dataRegion, username);
    }
}

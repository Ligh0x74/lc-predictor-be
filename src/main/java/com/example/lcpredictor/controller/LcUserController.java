package com.example.lcpredictor.controller;

import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.vo.Result;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class LcUserController {

    @Autowired
    LcUserService lcUserService;

    @PostMapping("/login/{dataRegion}/{username}")
    public Result<LcUserDTO> login(@PathVariable("dataRegion") String dataRegion,
                                   @PathVariable("username") String username,
                                   HttpSession session) throws InterruptedException {
        return lcUserService.login(dataRegion, username, session);
    }
}

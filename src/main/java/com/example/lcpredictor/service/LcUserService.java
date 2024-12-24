package com.example.lcpredictor.service;

import com.example.lcpredictor.domain.LcUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.vo.Result;

public interface LcUserService extends IService<LcUser> {

    /**
     * 登录功能
     *
     * @param dataRegion 数据区域
     * @param username   用户名
     * @return 用户信息
     */
    Result<LcUserDTO> login(String dataRegion, String username) throws InterruptedException;
}

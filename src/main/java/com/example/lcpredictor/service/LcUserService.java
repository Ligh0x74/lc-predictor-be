package com.example.lcpredictor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.vo.Result;
import jakarta.servlet.http.HttpSession;

public interface LcUserService extends IService<LcUser> {

    /**
     * 登录功能
     *
     * @param dataRegion 数据区域
     * @param username   用户名
     * @param session    会话对象
     * @return 用户信息
     * @throws InterruptedException 见 {@link Thread#sleep(long)}
     */
    Result<LcUserDTO> login(String dataRegion, String username, HttpSession session) throws InterruptedException;
}

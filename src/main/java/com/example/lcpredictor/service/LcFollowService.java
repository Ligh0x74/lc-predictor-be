package com.example.lcpredictor.service;

import com.example.lcpredictor.domain.LcFollow;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lcpredictor.vo.Result;

public interface LcFollowService extends IService<LcFollow> {

    /**
     * 关注/取关指定用户
     *
     * @param dataRegion 数据区域
     * @param username   用户名
     * @param isFollow   关注/取关
     * @return 返回空的对象
     */
    Result<?> follow(String dataRegion, String username, Boolean isFollow);
}

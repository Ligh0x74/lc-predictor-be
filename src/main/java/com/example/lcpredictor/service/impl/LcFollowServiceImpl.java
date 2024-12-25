package com.example.lcpredictor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcFollow;
import com.example.lcpredictor.mapper.LcFollowMapper;
import com.example.lcpredictor.service.LcFollowService;
import com.example.lcpredictor.vo.Result;
import org.springframework.stereotype.Service;

@Service
public class LcFollowServiceImpl extends ServiceImpl<LcFollowMapper, LcFollow>
        implements LcFollowService {

    @Override
    public Result<?> follow(String dataRegion, String username, Boolean follow) {

        return null;
    }
}

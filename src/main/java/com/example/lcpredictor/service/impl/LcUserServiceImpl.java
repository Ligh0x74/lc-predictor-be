package com.example.lcpredictor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcUser;
import com.example.lcpredictor.service.LcUserService;
import com.example.lcpredictor.mapper.LcUserMapper;
import org.springframework.stereotype.Service;

@Service
public class LcUserServiceImpl extends ServiceImpl<LcUserMapper, LcUser>
        implements LcUserService {
}

package com.example.lcpredictor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.service.LcContestService;
import com.example.lcpredictor.mapper.LcContestMapper;
import org.springframework.stereotype.Service;

@Service
public class LcContestServiceImpl extends ServiceImpl<LcContestMapper, LcContest>
        implements LcContestService {
}

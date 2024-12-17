package com.example.lcpredictor.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.service.LcPredictService;
import com.example.lcpredictor.mapper.LcPredictMapper;
import org.springframework.stereotype.Service;

@Service
public class LcPredictServiceImpl extends ServiceImpl<LcPredictMapper, LcPredict>
        implements LcPredictService {
}

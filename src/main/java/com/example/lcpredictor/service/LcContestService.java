package com.example.lcpredictor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lcpredictor.domain.LcContest;
import com.example.lcpredictor.dto.LcContestDTO;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;

public interface LcContestService extends IService<LcContest> {

    /**
     * 获取竞赛页面
     *
     * @param pageIndex 页号
     * @param pageSize  页面大小
     * @return 页面对象
     */
    Result<PageVo<LcContestDTO>> get(Integer pageIndex, Integer pageSize);
}

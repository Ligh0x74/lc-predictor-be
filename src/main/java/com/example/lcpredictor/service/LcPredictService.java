package com.example.lcpredictor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.lcpredictor.domain.LcPredict;
import com.example.lcpredictor.dto.LcPredictDTO;
import com.example.lcpredictor.vo.PageVo;
import com.example.lcpredictor.vo.Result;

public interface LcPredictService extends IService<LcPredict> {

    /**
     * 获取预测页面
     *
     * @param contestName 竞赛名称
     * @param pageIndex   页号
     * @param pageSize    页面大小
     * @return 页面对象
     */
    Result<PageVo<LcPredictDTO>> get(String contestName, Integer pageIndex, Integer pageSize);
}

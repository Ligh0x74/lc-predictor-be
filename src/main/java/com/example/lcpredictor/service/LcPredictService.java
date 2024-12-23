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

    /**
     * 获取指定用户名的预测列表
     *
     * @param contestName
     * @param username
     * @return 指定用户名的预测列表
     */

    /**
     * 获取指定的预测对象
     *
     * @param contestName 竞赛名称
     * @param dataRegion  数据区域
     * @param username    用户名
     * @return 预测对象
     */
    Result<LcPredictDTO> get(String contestName, String dataRegion, String username);
}

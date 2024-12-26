package com.example.lcpredictor.mapper;

import com.example.lcpredictor.domain.LcPredict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface LcPredictMapper extends BaseMapper<LcPredict> {

    @Select("""
            select * from lc_predictor.lc_predict
            where contest_id = #{contestId} and data_region = #{dataRegion} and username = #{username}
            """)
    LcPredict selectOneByContestIdAndDataRegionAndUsername(Integer contestId, String dataRegion, String username);
}

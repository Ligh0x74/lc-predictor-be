package com.example.lcpredictor.mapper;

import com.example.lcpredictor.domain.LcUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface LcUserMapper extends BaseMapper<LcUser> {

    @Select("""
            select * from lc_predictor.lc_user
            where data_region = #{dataRegion} and username = #{username}
            """)
    LcUser selectOneByDataRegionAndUsername(String dataRegion, String username);
}

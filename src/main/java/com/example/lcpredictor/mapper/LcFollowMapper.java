package com.example.lcpredictor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.lcpredictor.domain.LcFollow;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface LcFollowMapper extends BaseMapper<LcFollow> {

    @Select("""
            select * from lc_predictor.lc_follow
            where source_data_region = #{sourceDataRegion} and source_username = #{sourceUsername}
            and target_data_region = #{targetDataRegion} and target_username = #{targetUsername}
            """)
    LcFollow selectOneBySourceDataRegionAndSourceUsernameAndTargetDataRegionAndTargetUsername(
            String sourceDataRegion, String sourceUsername, String targetDataRegion, String targetUsername
    );

    @Delete("""
            delete from lc_predictor.lc_follow
            where source_data_region = #{sourceDataRegion} and source_username = #{sourceUsername}
            and target_data_region = #{targetDataRegion} and target_username = #{targetUsername}
            """)
    Boolean delBySourceDataRegionAndSourceUsernameAndTargetDataRegionAndTargetUsername(
            String sourceDataRegion, String sourceUsername, String targetDataRegion, String targetUsername
    );

    @Select("""
            select * from lc_predictor.lc_follow
            where source_data_region = #{dataRegion} and source_username = #{username}
            """)
    List<LcFollow> selectAllBySourceDataRegionAndSourceUsername(String dataRegion, String username);
}

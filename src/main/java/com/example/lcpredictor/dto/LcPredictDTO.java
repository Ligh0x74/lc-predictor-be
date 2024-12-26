package com.example.lcpredictor.dto;

import lombok.Data;

@Data
public class LcPredictDTO {

    /**
     * 数据区域: CN/US
     */
    private String dataRegion;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 排名
     */
    private Integer rank;

    /**
     * 参赛次数
     */
    private Integer attendedCount;

    /**
     * 当前评分
     */
    private Double oldRating;

    /**
     * 预测评分
     */
    private Double newRating;

    /**
     * 是否关注
     */
    private Boolean isFollow;
}

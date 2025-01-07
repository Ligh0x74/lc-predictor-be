package com.example.lcpredictor.dto;

import lombok.Data;

import java.util.Date;

@Data
public class LcContestDTO {

    /**
     * 竞赛名称
     */
    private String contestName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 预测时间
     */
    private Date predictTime;
}

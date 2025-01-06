package com.example.lcpredictor.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LcContestDTO {

    /**
     * 竞赛名称
     */
    private String contestName;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 预测时间
     */
    private LocalDateTime predictTime;
}

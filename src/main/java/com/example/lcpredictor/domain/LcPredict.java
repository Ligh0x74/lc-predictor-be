package com.example.lcpredictor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * LC 预测表
 */
@TableName(value = "lc_predict")
@Data
public class LcPredict implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 竞赛编号: 周赛场次 * 2 + 1, 双周赛场次 * 2
     */
    private Integer contestId;

    /**
     * 数据区域: CN/US
     */
    private String dataRegion;

    /**
     * 用户名
     */
    private String username;

    /**
     * 排名
     */
    private Integer ranking;

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
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

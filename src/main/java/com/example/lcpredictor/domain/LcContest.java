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
 * LC 竞赛表
 */
@TableName(value = "lc_contest")
@Data
public class LcContest implements Serializable {
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
     * 开始时间
     */
    private Date startTime;

    /**
     * 预测时间
     */
    private Date predictTime;

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

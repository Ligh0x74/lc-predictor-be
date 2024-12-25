package com.example.lcpredictor.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * LC 关注表
 */
@TableName(value = "lc_follow")
@Data
public class LcFollow implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 数据区域: CN/US
     */
    private String sourceDataRegion;

    /**
     * 用户名
     */
    private String sourceUsername;

    /**
     * 数据区域: CN/US
     */
    private String targetDataRegion;

    /**
     * 用户名
     */
    private String targetUsername;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    private LocalDateTime updateTime;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

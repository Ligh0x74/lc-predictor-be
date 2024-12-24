package com.example.lcpredictor.dto;

import lombok.Data;

@Data
public class LcUserDTO {

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
}

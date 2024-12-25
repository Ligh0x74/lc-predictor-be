package com.example.lcpredictor.utils;

import com.example.lcpredictor.dto.LcUserDTO;

/**
 * 线程数据
 */
public class ThreadLocals {

    public static final ThreadLocal<LcUserDTO> lcUserDTOThreadLocal = new ThreadLocal<>();
}

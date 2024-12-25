package com.example.lcpredictor.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 配置属性
 */
@Data
@Configuration
@ConfigurationProperties("lc-predictor")
public class Properties {

    private String[] allowedOrigins;
}

package com.example.lcpredictor.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    /**
     * 配置拦截器，执行顺序和添加顺序相同，或者使用 order 设置优先级
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
    }

    /**
     * 配置 CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*");
    }
}

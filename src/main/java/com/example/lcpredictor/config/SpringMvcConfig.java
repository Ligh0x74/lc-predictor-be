package com.example.lcpredictor.config;

import com.example.lcpredictor.interceptor.GlobalInterceptor;
import com.example.lcpredictor.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private PropertyConfig propertyConfig;

    @Autowired
    private GlobalInterceptor globalInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;

    /**
     * 配置拦截器，执行顺序和添加顺序相同，或者使用 order 设置优先级
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(globalInterceptor).addPathPatterns("/**");
        registry.addInterceptor(loginInterceptor)
                .excludePathPatterns(
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/contest/**",
                        "/predict/**",
                        "/user/**"
                );
    }

    /**
     * 配置 CORS
     * https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins(propertyConfig.getAllowedOrigins());
    }
}

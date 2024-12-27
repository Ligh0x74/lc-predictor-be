package com.example.lcpredictor.interceptor;

import cn.hutool.jwt.JWTUtil;
import com.example.lcpredictor.config.PropertyConfig;
import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.utils.ThreadLocals;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Autowired
    private PropertyConfig propertyConfig;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 直接拦截 OPTIONS 方法, 因为 CORS 预检请求不会携带 Authorization, 登录拦截器会返回 401, 会导致预检失败
        if (request.getMethod().equals("OPTIONS")) {
            return false;
        }
        // 获取、验证 token
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            return true;
        }
        boolean ok = JWTUtil.verify(token, propertyConfig.getJwtKey().getBytes(StandardCharsets.UTF_8));
        if (!ok) {
            return true;
        }
        // 获取 token 中的用户信息, 存储在 ThreadLocal 中
        LcUserDTO userDTO = JWTUtil.parseToken(token).getPayloads().toBean(LcUserDTO.class);
        ThreadLocals.lcUserDTOThreadLocal.set(userDTO);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 避免内存泄露
        ThreadLocals.lcUserDTOThreadLocal.remove();
    }
}

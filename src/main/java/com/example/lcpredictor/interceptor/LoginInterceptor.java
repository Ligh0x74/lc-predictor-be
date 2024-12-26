package com.example.lcpredictor.interceptor;

import com.example.lcpredictor.utils.ThreadLocals;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 拦截未登录的请求
        if (ThreadLocals.lcUserDTOThreadLocal.get() == null) {
            response.setStatus(401);
            return false;
        }
        return true;
    }
}

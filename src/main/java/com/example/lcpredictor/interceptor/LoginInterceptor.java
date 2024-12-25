package com.example.lcpredictor.interceptor;

import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.utils.ThreadLocals;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 session 中的用户信息, 如果不存在 (未登录), 则拦截请求
        HttpSession session = request.getSession();
        LcUserDTO userDTO = (LcUserDTO) session.getAttribute("userDTO");
        if (userDTO == null) {
            response.setStatus(401);
            return false;
        }
        // 将用户对象存储在 ThreadLocal 中
        ThreadLocals.lcUserDTOThreadLocal.set(userDTO);
        return true;
    }
}

package com.example.lcpredictor.interceptor;

import com.example.lcpredictor.dto.LcUserDTO;
import com.example.lcpredictor.utils.ThreadLocals;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取 session 中的用户信息, 如果存在 (已登录), 则存储在 ThreadLocal 中
        HttpSession session = request.getSession();
        LcUserDTO userDTO = (LcUserDTO) session.getAttribute("userDTO");
        if (userDTO != null) {
            ThreadLocals.lcUserDTOThreadLocal.set(userDTO);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 避免内存泄露
        ThreadLocals.lcUserDTOThreadLocal.remove();
    }
}

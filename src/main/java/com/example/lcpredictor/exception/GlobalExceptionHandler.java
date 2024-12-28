package com.example.lcpredictor.exception;

import com.example.lcpredictor.vo.Result;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 (TODO: 和 swagger 不兼容)
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e, HttpServletResponse response) {
        response.setStatus(500);
        log.error(e.getMessage(), e);
        return Result.error(e.getClass().toString());
    }
}

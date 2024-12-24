package com.example.lcpredictor.exception;

import com.example.lcpredictor.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器 (TODO: 和 swagger 不兼容)
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<?> handle(Exception e) {
        return Result.error(e.getClass().toString());
    }
}

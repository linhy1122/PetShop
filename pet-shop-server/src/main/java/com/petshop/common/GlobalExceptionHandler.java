package com.petshop.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Result<Object> handleBusinessException(BusinessException e) {
        if (log.isErrorEnabled()) {
            log.error("业务异常: {}", e.getMessage());
        }
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public Result<Object> handleRuntimeException(RuntimeException e) {
        if (log.isErrorEnabled()) {
            log.error("运行时异常: {}", e.getMessage());
        }
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Object> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("参数校验失败");
        return Result.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Object> handleException(Exception e) {
        if (log.isErrorEnabled()) {
            log.error("系统异常", e);
        }
        return Result.fail(500, "服务器内部错误");
    }
}

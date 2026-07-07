package com.petshop.common;

/**
 * 业务异常，用于替代通用 RuntimeException
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}

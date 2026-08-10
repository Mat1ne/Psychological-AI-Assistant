package com.matong.Admin.Exception;

public class BusinessException extends RuntimeException {
    private final String code;
    private final String msg;
    private final Object data;

    public BusinessException(String message) {
        super(message);
        this.msg = message;
        this.data = null;
        this.code = "BUSINESS_ERROR";
    }
}

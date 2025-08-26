package com.gov.core.exception;


public class BusinessException extends RuntimeException{

    private  Integer code;

    public Integer getCode() {
        return code;
    }
    public void setCode(Integer code) {
        this.code = code;
    }

    public BusinessException( String message ) {
        super(message);
        this.code = 500;
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}

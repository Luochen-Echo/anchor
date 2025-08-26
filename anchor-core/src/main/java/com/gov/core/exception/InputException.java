package com.gov.core.exception;

/**
 * @Description 输入数据异常
 * @Author Chen Shu
 * @Date 2023/2/7 20:47
 * @Company Hydroy(Leshui)
 * @Verision
 * @Notices
 */
public class InputException extends RuntimeException{

    /**
     * 异常码
     */
    private  Integer code;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public InputException(Integer code, String message ) {
        super(message);
        this.code = code;
    }

    public InputException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}

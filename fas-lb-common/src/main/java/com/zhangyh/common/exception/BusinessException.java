package com.zhangyh.common.exception;

/**
 * @author zhangyh
 * @Date 2023/3/6 15:38
 * @desc
 */
public class BusinessException extends RuntimeException{
    private  ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode,String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String message) {
        super(message);
    }
    public BusinessException(Throwable cause, String message) {
        super(message,cause);
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

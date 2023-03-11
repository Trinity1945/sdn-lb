package com.zhangyh.common.http.respose;


import com.zhangyh.common.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyh
 * @Date 2023/3/6 15:24
 * @desc
 */
@Data
public class BaseResponse<T> implements Serializable {

    private static final long serialVersionUID = 31415926535L;
    private String msg;

    private int code;

    private T data;

    public BaseResponse(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public BaseResponse(String msg, int code, T data) {
        this.msg = msg;
        this.code = code;
        this.data = data;
    }

    public BaseResponse(String msg) {
        this.msg = msg;
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getMessage(),errorCode.getCode(),null);
    }

    public BaseResponse(ErrorCode errorCode,T data) {
        this(errorCode.getMessage(),errorCode.getCode(),data);
    }
}

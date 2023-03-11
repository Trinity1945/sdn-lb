package com.zhangyh.common.exception;

/**
 * @author zhangyh
 * @Date 2023/3/6 15:31
 * @desc
 */
public enum ErrorCode {
    /**
     *
     */
    SUCCESS(200,"OK"),
    PARAMS_INVALID(405,"非法参数"),
    DATA_NOT_EXIST(406,"数据不存在"),
    BUSINESS_ERROR(408,"请求错误"),
    PARAMS_ERROR(550,"请求参数错误"),
    MISSING_PARAMS(409,"参数缺失"),
    NO_AUTH_ERROR(503,"无权限" ),
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),

    ;

    private final String message;

    private final int code;

    ErrorCode(int code, String message) {
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }
}

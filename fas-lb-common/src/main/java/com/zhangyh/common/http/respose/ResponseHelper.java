package com.zhangyh.common.http.respose;


import com.zhangyh.common.exception.ErrorCode;

/**
 * @author zhangyh
 * @Date 2023/3/6 15:40
 * @desc
 */
public class ResponseHelper {

    /**
     * 请求成功
     * @param data 请求数据
     * @return 统一响应数据
     * @param <T> 数据类型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(ErrorCode.SUCCESS,data);
    }

    public static <T> BaseResponse<T> failed(){
        return new BaseResponse<>(ErrorCode.SYSTEM_ERROR);
    }

    public static <T> BaseResponse<T> failed(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> failed(String message){
        return new BaseResponse<>(message);
    }

    public static <T> BaseResponse<T> failed(ErrorCode errorCode,T data){
        return new BaseResponse<>(errorCode,data);
    }

    public static <T> BaseResponse<T> failed(int code,String msg){
        return new BaseResponse<>(msg,code,null);
    }
}

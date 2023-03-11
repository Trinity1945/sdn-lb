package com.zhangyh.common.exception;

/**
 * @Author: 李子园
 * <p>
 * Date: 2022/11/11  21:14
 */
public class Asserts {
    public static void fail(String message) {
        throw new BusinessException(message);
    }

    public static void fail(ErrorCode errorCode) {
        throw new BusinessException(errorCode);
    }
}

package com.zhangyh.logging.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @Author: zhangyh
 * @desc 异常工具
 * @date: 2023/3/11  8:59
 */
public class ThrowableUtil {
    /**
     * 获取堆栈信息
     */
    public static String getStackTrace(Throwable throwable){
        StringWriter sw = new StringWriter();
        try (PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
    }
}

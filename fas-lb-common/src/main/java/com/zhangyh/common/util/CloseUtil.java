package com.zhangyh.common.util;

import java.io.Closeable;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/18  9:44
 */
public class CloseUtil {
    public static void close(Closeable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }

    public static void close(AutoCloseable closeable) {
        if (null != closeable) {
            try {
                closeable.close();
            } catch (Exception e) {
                // 静默关闭
            }
        }
    }
}

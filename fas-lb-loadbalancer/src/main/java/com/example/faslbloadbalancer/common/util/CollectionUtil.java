package com.example.faslbloadbalancer.common.util;

import java.util.Collection;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:19
 */
public class CollectionUtil {
    public static Boolean isEmpty(Collection<?> c){
        return c==null||c.isEmpty();
    }
}

package com.zhangyh.common.annotation;


import com.zhangyh.common.aspect.LimitType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: zhangyh
 * @desc 接口资源限制
 * @date: 2023/3/13  22:06
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    //资源名称。用于描述接口功能
    String name() default "";

    //资源 key
    String key() default "";

    //key prefix
    String prefix() default "";

    //时间--单位秒
    int period();

    //限制次数
    int count();

    // 限制类型
    LimitType limitType() default LimitType.CUSTOMER;
}

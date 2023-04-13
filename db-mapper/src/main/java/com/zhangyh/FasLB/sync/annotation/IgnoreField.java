package com.zhangyh.FasLB.sync.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhangyh
 * @Date 2023/4/10 14:47
 * @desc
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface IgnoreField {

    /**
     * 忽略字段
     */
    @AliasFor("ignore")
    boolean value() default true;

    /**
     * 忽略字段
     */
    @AliasFor("value")
    boolean ignore() default true;
}

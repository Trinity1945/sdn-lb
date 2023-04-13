package com.zhangyh.FasLB.sync.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhangyh
 * @Date 2023/4/10 10:54
 * @desc 表信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface TableSync {

    /**
     * 表名
     * 同tableName
     */
    @AliasFor("tableName")
    String value() default "";

    /**
     * 表名
     * 同Value
     */
    @AliasFor("value")
    String tableName() default "";

    /**
     * 表注释
     */
    String tableComment() default "";

    /**
     * 允许删除数据库中存在但是实体中不存在的字段
     * @return
     */
    boolean delOldField() default true;
}

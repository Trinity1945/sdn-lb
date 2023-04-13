package com.zhangyh.FasLB.sync.annotation;

import com.zhangyh.FasLB.sync.sql.table.Index;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;
import java.sql.JDBCType;

/**
 * @author zhangyh
 * @Date 2023/4/10 10:54
 * @desc 字段信息
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Field {

    /**
     * 数据库对应字段名
     */
    @AliasFor("field")
    String value() default "";

    /**
     * 数据库对应字段名
     */
    @AliasFor("value")
    String field() default "";

    /**
     * 数据库对应数据类型 不配置则自动根据类型生成
     */
    JDBCType jdbcType() default JDBCType.NULL;

    /**
     * 字段的注释
     */
    String comment() default "";

    /**
     * 数据类型长度 当length为1024时数据类型为LONGTEXT
     */
    int length() default 255;

    /**
     * 小数点位数 只有double、Float生效
     */
    int decimalPoint() default 3;

    /**
     * 默认值
     */
    String defaultValue() default "";

    /**
     * 允许为空
     */
    boolean allowNull() default true;

    /**
     * 自增
     */
    boolean autoIncrement() default false;

    /**
     * 索引类型
     */
    Class<? extends Index> index() default Index.class;

}

package com.zhangyh.FasLB.sync.annotation;

import com.zhangyh.FasLB.sync.core.SyncScannerRegistry;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author zhangyh
 * @Date 2023/4/10 9:29
 * @desc 开启表结构同步
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.TYPE)
@Import(SyncScannerRegistry.class)
public @interface EnableTableSync {

    /**
     * 扫描的包 同basePackages
     */
    @AliasFor("basePackages")
    String[] value() default {};

    /**
     * 扫描的包 同value
     */
    @AliasFor("value")
    String[] basePackages() default {};

    /**
     * 扫描的类
     */
    Class<?>[] basePackageClasses() default {};

    /**
     * 扫描的注解 无需配置
     */
    Class<? extends Annotation> annotationClass() default Annotation.class;
}

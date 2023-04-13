package com.zhangyh.FasLB.sync.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhangyh
 * @Date 2023/4/10 9:35
 * @desc
 */
public class ClassPathSyncScanner extends ClassPathBeanDefinitionScanner {

    public static final Logger LOGGER = LoggerFactory.getLogger(ClassPathSyncScanner.class);

    private Class<? extends Annotation> annotationClass;

    public ClassPathSyncScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public ClassPathSyncScanner(BeanDefinitionRegistry registry, Class<? extends Annotation> annotationClass) {
        super(registry, false);
        this.annotationClass = annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public void registerFilters() {
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
        }
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No @SyncTable was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            processBeanDefinitions(beanDefinitions);
        }
        return beanDefinitions;
    }

    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        AtomicInteger atomicInteger = new AtomicInteger();
        for (BeanDefinitionHolder holder : beanDefinitions) {
            AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) holder.getBeanDefinition();
            AnnotationMetadata annotationMeta = beanDefinition.getMetadata();
            //不能是一个接口
            Assert.isTrue(!annotationMeta.isInterface(), " cannot be placed on an interface");
            atomicInteger.getAndIncrement();
        }
        LOGGER.info("SqlSync注解描的数量 [{}]", atomicInteger.get());
    }
}

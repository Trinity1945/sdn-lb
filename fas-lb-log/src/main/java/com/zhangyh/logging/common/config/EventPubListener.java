package com.zhangyh.logging.common.config;

import com.zhangyh.logging.admin.model.po.Log;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: zhangyh
 * @desc 事件发布
 * @date: 2023/3/12  23:29
 */
@Component
public class EventPubListener {

    @Resource
    private ApplicationContext applicationContext;

    /**
     * 事件发布
     * @param logEvent 事件
     */
    public void pushListener(Log logEvent){
        applicationContext.publishEvent(logEvent);
    }
}

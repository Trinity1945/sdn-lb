package com.zhangyh.logging.admin.service;

import com.zhangyh.logging.admin.model.po.Log;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  8:03
 */
public interface LogService {

    /**
     * 保存日志数据
     * @param username 用户
     * @param browser 浏览器
     * @param ip 请求IP
     * @param joinPoint /
     * @param log  日志实体
     */
    @Async
    void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log);
}

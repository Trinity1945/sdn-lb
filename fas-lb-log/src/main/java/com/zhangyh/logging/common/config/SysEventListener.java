package com.zhangyh.logging.common.config;

import com.zhangyh.logging.admin.model.po.Log;
import com.zhangyh.logging.admin.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangyh
 * @desc 事件监听异步处理
 * @date: 2023/3/12  23:33
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SysEventListener {

    private final LogService logService;

    @Async
    @EventListener(Log.class)
    public void saveSysLog(Log event) throws InterruptedException {
        log.info("====异步保存日志记录到数据库");
        logService.save(event);
    }
}

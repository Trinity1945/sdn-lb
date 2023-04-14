package com.example.faslbloadbalancer.admin.service;

import com.google.gson.Gson;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhangyh
 * @Date 2023/4/13 17:59
 * @desc
 */

@Slf4j
@Component
public class XxlJobService {
    /**
     * 1、简单任务示例（Bean模式）
     */
    @XxlJob("customJobHandler")
    public ReturnT<String> customJobHandler(String param) throws Exception {
        log.info("## custom job handler run. ##");
        log.info("param: {}", new Gson().toJson(param));
        XxlJobHelper.log("XXL-JOB, Hello World.");
        for (int i = 0; i < 5; i++) {
            XxlJobHelper.log("beat at:" + i);
            TimeUnit.SECONDS.sleep(2);
        }
        return ReturnT.SUCCESS;
    }
}

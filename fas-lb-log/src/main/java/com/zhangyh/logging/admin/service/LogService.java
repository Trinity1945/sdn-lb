package com.zhangyh.logging.admin.service;

import com.zhangyh.logging.admin.model.dto.LogQueryCriteria;
import com.zhangyh.logging.admin.model.po.Log;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  8:03
 */
public interface LogService {

    /**
     * 保存日志数据
     * @param log  日志实体
     */
    @Async
    void save( Log log);

    /**
     * 查询全部数据
     * @param criteria 查询条件
     * @return /
     */
    List<Log> queryAll(LogQueryCriteria criteria);
}

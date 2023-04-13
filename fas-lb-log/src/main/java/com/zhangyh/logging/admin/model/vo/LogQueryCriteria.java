package com.zhangyh.logging.admin.model.vo;

import com.zhangyh.logging.common.anotation.Query;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * @Author: zhangyh
 * @desc 日志查询类
 * @date: 2023/3/11  8:07
 */
@Data
public class LogQueryCriteria {

    @Query(blurry = "username,description,address,requestIp,method,params")
    private String blurry;

    @Query
    private String username;

    @Query
    private String logType;

    @Query(type = Query.Type.BETWEEN)
    private List<Timestamp> createTime;
}

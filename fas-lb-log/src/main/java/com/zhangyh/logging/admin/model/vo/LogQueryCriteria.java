package com.zhangyh.logging.admin.model.vo;

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

    private String blurry;

    private String userAccount;

    private String logType;

    private List<Timestamp> createTime;
}

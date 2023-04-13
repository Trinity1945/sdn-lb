package com.zhangyh.FasLB.model;

import com.zhangyh.FasLB.sync.annotation.Field;
import com.zhangyh.FasLB.sync.annotation.TableSync;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.PRI;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  7:58
 */
@Data
@TableSync(tableName = "fas_log",tableComment = "系统日志表")
@Table(name = "fas_log")
@NoArgsConstructor
@AllArgsConstructor
public class Log  implements Serializable {

    @Id
    @Field(field = "log_id",comment = "主键id",index = PRI.class,autoIncrement = true,allowNull = false)
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Field(field = "user_account",comment = "操作用户--账号")
    @Column(name = "user_account")
    private String userAccount;

    @Field(field = "description",comment = "描述")
    @Column(name = "description")
    private String description;

    @Field(field = "method",comment = "操作的方法名")
    @Column(name = "method")
    private String method;

    @Field(field = "params",comment = "请求参数")
    @Column(name = "params")
    private String params;

    @Field(field = "log_type",comment = "日志类型")
    @Column(name = "log_type")
    private String logType;

    @Field(field = "request_ip",comment = "请求ip")
    @Column(name = "request_ip")
    private String requestIp;

    @Field(field = "address",comment = "地址")
    @Column(name = "address")
    private String address;

    @Field(field = "browser",comment = "浏览器")
    @Column(name = "browser")
    private String browser;

    @Field(field = "time",comment = "请求耗时")
    @Column(name = "time")
    private Long time;

    @Field(field = "exception_detail",comment = "异常详细")
    @Column(name = "exception_detail")
    private byte[] exceptionDetail;

    @Field(field = "create_time",comment = "创建日期")
    @Column(name = "create_time")
    private Date createTime;

    public Log(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}

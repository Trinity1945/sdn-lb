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

    public static final String ID="id";
    public static final String USER_ACCOUNT="userAccount";
    public static final String DESCRIPTION="description";
    public static final String METHOD="method";
    public static final String PARAMS="params";
    public static final String LOG_TYPE="logType";
    public static final String BUSINESS_TYPE="businessType";
    public static final String REQUEST_IP="requestIp";
    public static final String ADDRESS="address";
    public static final String BROWSER="browser";
    public static final String TIME="time";
    public static final String EXCEPTION_DETAIL="exceptionDetail";
    public static final String CREATE_TIME="create_time";

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

    @Field(field = "business_type",comment = "业务操作类型")
    @Column(name = "business_type")
    private String businessType;

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

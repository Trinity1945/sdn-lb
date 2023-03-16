package com.zhangyh.logging.admin.model.po;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  7:58
 */
@Entity
@Getter
@Setter
@Table(name = "fas_log")
@NoArgsConstructor
public class Log  implements Serializable {

    @Id
    @Column(name = "log_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 操作用户 */
    @Column(name = "userAccount")
    private String userAccount;

    /** 描述 */
    @Column(name = "description")
    private String description;

    /** 方法名 */
    @Column(name = "method")
    private String method;

    /** 参数 */
    @Column(name = "params")
    private String params;

    /** 日志类型 */
    @Column(name = "log_type")
    private String logType;

    /** 请求ip */
    @Column(name = "request_ip")
    private String requestIp;

    /** 地址 */
    @Column(name = "address")
    private String address;

    /** 浏览器  */
    @Column(name = "browser")
    private String browser;

    /** 请求耗时 */
    @Column(name = "time")
    private Long time;

    /** 异常详细  */
    @Column(name = "exception_detail")
    private byte[] exceptionDetail;

    /** 创建日期 */
    @Column(name = "create_time")
    private Timestamp createTime;

    public Log(String logType, Long time) {
        this.logType = logType;
        this.time = time;
    }
}

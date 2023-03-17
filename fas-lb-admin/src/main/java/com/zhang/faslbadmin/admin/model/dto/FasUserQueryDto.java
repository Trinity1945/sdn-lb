package com.zhang.faslbadmin.admin.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/16  22:45
 */
@Data
public class FasUserQueryDto {


    private Integer id;
    /**
     * 账号
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;
    /**
     * ip地址
     */
    private String ip;

    private Integer state;

    private Date startTime;

    private Date endTime;

    private Integer limit;

    private Integer page;
}

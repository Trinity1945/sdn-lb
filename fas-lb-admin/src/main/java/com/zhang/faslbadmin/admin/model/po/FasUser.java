package com.zhang.faslbadmin.admin.model.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  19:49
 */
@Data
@ApiModel(value = "用户账户表",description = "账户信息实体")
@Table(name="fas_user")
public class FasUser implements Serializable {
    /**
     * 主键id
     */
    @Id
    @ApiModelProperty(name = "主键id")
    private Integer id;
    /**
     * 账号
     */
    @ApiModelProperty(name = "账号")
    private String userAccount;
    /**
     * 密码
     */
    @ApiModelProperty(name = "密码")
    private String password;
    /**
     * 头像
     */
    @ApiModelProperty(name = "头像")
    private String avatar;
    /**
     * ip地址
     */
    @ApiModelProperty(name = "ip地址")
    private String ip;
    /**
     * 账号状态
     */
    @ApiModelProperty(name = "账号状态")
    private Integer state;
    /**
     * 创建时间
     */
    @ApiModelProperty(name = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @ApiModelProperty(name = "更新时间")
    private Date updateTime;
}
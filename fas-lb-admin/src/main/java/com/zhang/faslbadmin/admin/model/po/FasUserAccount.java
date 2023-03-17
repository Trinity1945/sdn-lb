package com.zhang.faslbadmin.admin.model.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
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
@Table(name="fas_user_account")
@NoArgsConstructor
@AllArgsConstructor
public class FasUserAccount implements Serializable {
    /**
     * 主键id
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(name = "主键id")
    private Integer id;
    /**
     * 账号
     */
    @Column(name = "username")
    @ApiModelProperty(name = "账号")
    private String username;
    /**
     * 密码
     */
    @Column(name = "password")
    @ApiModelProperty(name = "密码")
    private String password;
    /**
     * 头像
     */
    @Column(name = "avatar")
    @ApiModelProperty(name = "头像")
    private String avatar;
    /**
     * ip地址
     */
    @Column(name = "ip")
    @ApiModelProperty(name = "ip地址")
    private String ip;
    /**
     * 账号状态
     */
    @Column(name = "state")
    @ApiModelProperty(name = "账号状态")
    private Integer state;

    /** 是否删除 */
    @Column(name = "deleted")
    @ApiModelProperty(name = "是否删除",notes = "")
    private String deleted ;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    @ApiModelProperty(name = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(name = "更新时间")
    private Date updateTime;

    /** 过期时间 */
    @Column(name = "expired_time")
    @ApiModelProperty(name = "过期时间",notes = "")
    private Date expiredTime ;
}
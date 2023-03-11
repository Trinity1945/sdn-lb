package com.zhang.faslbadmin.admin.model.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  19:49
 */

@ApiModel(value = "用户表",description = "")
@Data
@Table(name = "user")
public class User {
    /** 主键 */
    @Id
    @Column(name = "id")
    @ApiModelProperty(name = "主键",notes = "")
    private Integer id ;
    /** 用户账户 */
    @Column(name = "userAccount")
    @ApiModelProperty(name = "用户账户",notes = "")
    private String userAccount ;
    /** 用户名 */
    @Column(name = "username")
    @ApiModelProperty(name = "用户名",notes = "")
    private String username ;
    /** 用户密码 */
    @Column(name = "password")
    @ApiModelProperty(name = "用户密码",notes = "")
    private String password ;
    /** 账号状态（0正常1停用） */
    @Column(name = "state")
    @ApiModelProperty(name = "账号状态（0正常1停用）",notes = "")
    private Integer state ;
    /** 邮箱 */
    @Column(name = "email")
    @ApiModelProperty(name = "邮箱",notes = "")
    private String email ;
    /** 手机号 */
    @Column(name = "phoneNumber")
    @ApiModelProperty(name = "手机号",notes = "")
    private String phoneNumber ;
    /** IPv4地址 */
    @Column(name = "ipv4")
    @ApiModelProperty(name = "IPv4地址",notes = "")
    private String ipv4 ;
    /** IPv6地址 */
    @Column(name = "ipv6")
    @ApiModelProperty(name = "IPv6地址",notes = "")
    private String ipv6 ;
    /** 性别 */
    @Column(name = "gender")
    @ApiModelProperty(name = "性别",notes = "")
    private String gender ;
    /** 头像 */
    @Column(name = "avatar")
    @ApiModelProperty(name = "头像",notes = "")
    private String avatar ;
    /** 用户类别 */
    @Column(name = "usertype")
    @ApiModelProperty(name = "用户类别",notes = "")
    private String usertype ;
    /** 创建时间 */
    @Column(name = "createTime")
    @ApiModelProperty(name = "创建时间",notes = "")
    private Date createTime ;
    /** 更新时间 */
    @Column(name = "updateTime")
    @ApiModelProperty(name = "更新时间",notes = "")
    private Date updateTime ;
    /** 逻辑删除 */
    @Column(name = "isDelete")
    @ApiModelProperty(name = "逻辑删除",notes = "")
    private Integer isDelete ;
}

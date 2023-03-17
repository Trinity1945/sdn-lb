package com.zhang.faslbadmin.admin.model.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/16  21:08
 */
@Data
@ApiModel(value = "用户信息表",description = "")
@Table(name="fas_user_info")
public class FasUserinfo implements Serializable {
    /**
     * 主键id
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(name = "主键id")
    private Integer id;
    /**
     * 用户名
     */
    @Column(name = "name")
    @ApiModelProperty(name = "用户名")
    private String name;
    /**
     * 用户账户id
     */
    @Column(name = "account_id")
    @ApiModelProperty(name = "用户账户id")
    private Integer accountId;
    /**
     * 性别
     */
    @Column(name = "gender")
    @ApiModelProperty(name = "性别")
    private String gender;
    /**
     * 邮箱
     */
    @Column(name = "email")
    @ApiModelProperty(name = "邮箱")
    private String email;
    /**
     * 手机
     */
    @Column(name = "mobile")
    @ApiModelProperty(name = "手机")
    private String mobile;
    /**
     * 用户状态
     */
    @Column(name = "deleted")
    @ApiModelProperty(name = "用户状态")
    private Integer deleted;
    /**
     * 用户生日
     */
    @Column(name = "birthday")
    @ApiModelProperty(name = "用户状态")
    private Date birthday;
    /**
     * 创建时间
     */
    @Column(name = "createTime")
    @ApiModelProperty(name = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "updateTime")
    @ApiModelProperty(name = "更新时间")
    private Date updateTime;
}

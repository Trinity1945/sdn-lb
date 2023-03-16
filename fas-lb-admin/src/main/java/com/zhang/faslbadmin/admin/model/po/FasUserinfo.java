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
 * @date: 2023/3/16  21:08
 */
@Data
@ApiModel(value = "用户信息表",description = "")
@Table(name="fas_userInfo")
public class FasUserinfo implements Serializable {
    /**
     * 主键id
     */
    @Id
    @ApiModelProperty(name = "主键id")
    private Integer id;
    /**
     * 用户名
     */
    @ApiModelProperty(name = "用户名")
    private String userName;
    /**
     * 用户账户id
     */
    @ApiModelProperty(name = "用户账户id")
    private Integer accountId;
    /**
     * 性别
     */
    @ApiModelProperty(name = "性别")
    private String gender;
    /**
     * 邮箱
     */
    @ApiModelProperty(name = "邮箱")
    private String email;
    /**
     * 手机
     */
    @ApiModelProperty(name = "手机")
    private String phone;
    /**
     * 用户状态
     */
    @ApiModelProperty(name = "用户状态")
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

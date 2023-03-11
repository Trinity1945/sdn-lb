package com.zhang.faslbadmin.admin.model.dto;

import com.zhang.faslbadmin.common.valid.LoginGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  21:48
 */
@Getter
@Setter
@ApiModel("用户登录参数")
public class UserDto {
    @NotEmpty(message = "账号不能为空",groups = {LoginGroup.class})
    @ApiModelProperty(value = "账号", required = true)
    private String userAccount;

    @NotEmpty(message = "密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}

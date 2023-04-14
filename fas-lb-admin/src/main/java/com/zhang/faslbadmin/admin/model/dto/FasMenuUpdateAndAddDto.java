package com.zhang.faslbadmin.admin.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author zhangyh
 * @Date 2023/4/14 17:24
 * @desc
 */
@Data
public class FasMenuUpdateAndAddDto {
    @NotNull(message = "主键不能为空")
    private Integer menuId;

    private Integer menuOrder;

    private Integer pid;

    private Integer subCount;

    private Integer type;

    private String title;

    private String icon;

    private String iconColor;

    private String path;

    private String component;

    private String name;

    private Byte hidden;
}

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
 * @date: 2023/3/19  18:24
 */
@Data
@ApiModel(value = "前端菜单表", description = "")
@Table(name = "fas_menu")
public class FasMenu implements Serializable {
    /**
     * 菜单id
     */
    @Id
    @ApiModelProperty(name = "菜单id", notes = "")
    private Integer menuId;
    /**
     * 菜单序号
     */
    @Column(name = "menu_order")
    @ApiModelProperty(name = "菜单序号", notes = "")
    private Integer menuOrder;
    /**
     * 上级菜单ID
     */
    @Column(name = "pid")
    @ApiModelProperty(name = "上级菜单ID", notes = "")
    private Integer pid;
    /**
     * 子菜单数目
     */
    @Column(name = "sub_count")
    @ApiModelProperty(name = "子菜单数目", notes = "")
    private Integer subCount;
    /**
     * 菜单类型
     */
    @Column(name = "type")
    @ApiModelProperty(name = "菜单类型", notes = "")
    private Integer type;
    /**
     * 菜单标题
     */
    @Column(name = "title")
    @ApiModelProperty(name = "菜单标题", notes = "")
    private String title;
    /**
     * 菜单图标
     */
    @Column(name = "icon")
    @ApiModelProperty(name = "菜单图标", notes = "")
    private String icon;

    /**
     * 菜单图标颜色
     */
    @Column(name = "icon_color")
    @ApiModelProperty(name = "菜单图标", notes = "")
    private String iconColor;
    /**
     * 路由
     */
    @Column(name = "path")
    @ApiModelProperty(name = "路由", notes = "")
    private String path;
    /**
     * 组件
     */
    @Column(name = "component")
    @ApiModelProperty(name = "组件", notes = "")
    private String component;
    /**
     * 组件名称
     */
    @Column(name = "name")
    @ApiModelProperty(name = "组件名称", notes = "")
    private String name;
    /**
     * 是否隐藏
     */
    @Column(name = "hidden")
    @ApiModelProperty(name = "是否隐藏", notes = "")
    private Boolean hidden;
    /**
     * 创建者
     */
    @Column(name = "create_by")
    @ApiModelProperty(name = "创建者", notes = "")
    private String createBy;
    /**
     * 更新者
     */
    @Column(name = "update_by")
    @ApiModelProperty(name = "更新者", notes = "")
    private String updateBy;
    /**
     * 创建日期
     */
    @Column(name = "create_time")
    @ApiModelProperty(name = "创建日期", notes = "")
    private Date createTime;
    /**
     * 更新时间
     */
    @Column(name = "update_time")
    @ApiModelProperty(name = "更新时间", notes = "")
    private Date updateTime;

}

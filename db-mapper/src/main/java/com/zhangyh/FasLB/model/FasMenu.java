package com.zhangyh.FasLB.model;

import com.zhangyh.FasLB.sync.annotation.Field;
import com.zhangyh.FasLB.sync.annotation.TableSync;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.PRI;
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
@TableSync(tableName = "fas_menu",tableComment = "前端菜单信息表",delOldField = false)
@Data
@Table(name = "fas_menu")
public class FasMenu implements Serializable {

    public static final String MENU_ID="menuId";
    public static final String PID="pid";
    public static final String SUB_COUNT="subCount";
    public static final String TYPE="type";
    public static final String TITLE="title";
    public static final String ICON="icon";
    public static final String ICON_COLOR="iconColor";
    public static final String PATH="path";
    public static final String COMPONENT="component";
    public static final String NAME="name";
    public static final String HIDDEN="hidden";
    public static final String CREATE_BY="createBy";
    public static final String UPDATE_BY="updateBy";
    public static final String DELETED="deleted";
    public static final String CREATE_TIME="createTime";
    public static final String UPDATE_TIME="updateTime";

    @Field(field = "menu_id",comment = "菜单Id",index = PRI.class,autoIncrement = true,allowNull = false)
    @Id
    private Integer menuId;

    @Field(field = "menu_order",comment = "菜单序号")
    @Column(name = "menu_order")
    private Integer menuOrder;

    @Field(field = "pid",comment = "上级菜单ID")
    @Column(name = "pid")
    private Integer pid;

    @Field(field = "sub_count",comment = "子菜单数目")
    @Column(name = "sub_count")
    private Integer subCount;

    @Field(field = "type",comment = "菜单类型")
    @Column(name = "type")
    private Integer type;

    @Field(field = "title",comment = "菜单标题")
    @Column(name = "title")
    private String title;

    @Field(field = "icon",comment = "菜单图标")
    @Column(name = "icon")
    private String icon;

    @Field(field = "icon_color",comment = "菜单图标颜色")
    @Column(name = "icon_color")
    private String iconColor;

    @Field(field = "path",comment = "路由")
    @Column(name = "path")
    private String path;

    @Field(field = "component",comment = "组件")
    @Column(name = "component")
    private String component;

    @Field(field = "name",comment = "组件名称")
    @Column(name = "name")
    private String name;

    @Field(field = "hidden",comment = "是否隐藏")
    @Column(name = "hidden")
    private Byte hidden;

    @Field(field = "create_by",comment = "创建者")
    @Column(name = "create_by")
    private String createBy;

    @Field(field = "update_by",comment = "更新者")
    @Column(name = "update_by")
    private String updateBy;

    @Field(field = "deleted",comment = "是否删除 0否1是",defaultValue = "0",allowNull = false)
    @Column(name = "deleted")
    private Byte deleted;

    @Field(field = "create_time",comment = "创建日期")
    @Column(name = "create_time")
    private Date createTime;

    @Field(field = "update_time",comment = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;
}

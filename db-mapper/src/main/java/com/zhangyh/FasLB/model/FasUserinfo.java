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
 * @date: 2023/3/16  21:08
 */
@TableSync(tableName = "fas_user_info",tableComment = "用户信息表",delOldField = false)
@Data
@Table(name="fas_user_info")
public class FasUserinfo implements Serializable {
    /**
     * 主键id
     */
    @Id
    @Field(field = "id",comment = "主键id",index = PRI.class,autoIncrement = true,allowNull = false)
    @Column(name = "id")
    private Integer id;
    /**
     * 用户名
     */
    @Field(field = "name",comment = "用户名字")
    @Column(name = "name")
    private String name;
    /**
     * 用户账户id
     */
    @Field(field = "account_id",comment = "用户账户id 关联account表")
    @Column(name = "account_id")
    private Integer accountId;

    @Field(field = "gender",comment = "性别")
    @Column(name = "gender")
    private String gender;

    @Field(field = "email",comment = "邮箱")
    @Column(name = "email")
    private String email;

    @Field(field = "mobile",comment = "手机")
    @Column(name = "mobile")
    private String mobile;

    @Field(field = "deleted",comment = "是否删除 0否 1是")
    @Column(name = "deleted")
    private Integer deleted;

    @Field(field = "birthday",comment = "用户生日")
    @Column(name = "birthday")
    private Date birthday;

    @Field(field = "create_time",comment = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @Field(field = "update_time",comment = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;
}

package com.zhangyh.FasLB.model;

import com.zhangyh.FasLB.sync.annotation.Field;
import com.zhangyh.FasLB.sync.annotation.TableSync;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.PRI;
import com.zhangyh.FasLB.sync.sql.table.mysql.index.UNI;
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
@TableSync(tableName = "fas_user_account",tableComment = "用户账户表",delOldField = false)
@Table(name="fas_user_account")
@NoArgsConstructor
@AllArgsConstructor
public class FasUserAccount implements Serializable {

    @Id
    @Field(field = "id",comment = "主键id",index = PRI.class,autoIncrement = true,allowNull = false)
    @Column(name = "id")
    private Integer id;

    @Field(field = "username",index = UNI.class,comment = "账号")
    @Column(name = "username")
    private String username;

    @Field(field = "password",comment = "密码")
    @Column(name = "password")
    private String password;

    @Field(field = "avatar",comment = "头像")
    @Column(name = "avatar")
    private String avatar;

    @Field(field = "ip",comment = "ip地址")
    @Column(name = "ip")
    private String ip;

    @Field(field = "state",comment = "账号状态 0在线 1离线 2封禁")
    @Column(name = "state")
    private Integer state;

    @Field(field = "deleted",comment = "是否删除")
    @Column(name = "deleted")
    private Integer deleted ;

    @Field(field = "create_time",comment = "创建时间")
    @Column(name = "create_time")
    private Date createTime;

    @Field(field = "update_time",comment = "更新时间")
    @Column(name = "update_time")
    private Date updateTime;

    @Field(field = "expired_time",comment = "过期时间")
    @Column(name = "expired_time")
    private Date expiredTime ;
}
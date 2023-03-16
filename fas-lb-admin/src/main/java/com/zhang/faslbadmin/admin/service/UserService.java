package com.zhang.faslbadmin.admin.service;

import com.zhang.faslbadmin.admin.model.po.FasUser;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  20:12
 */
public interface UserService {

    /**
     * 登录
     * @param userAccount 账号
     * @param password 密码
     * @return token
     */
    String login(String userAccount, String password);

    /**
     * 获取用户信息
     * @param userAccount 账号
     * @return u
     */
    UserDetails loadUserByUserAccount(String userAccount);

    /***
     * 数据库查询用户信息
     * @param userAccount 账号
     * @return User
     */
     FasUser getAdminByUserAccount(String userAccount);


}

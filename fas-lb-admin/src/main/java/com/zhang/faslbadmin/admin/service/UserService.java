package com.zhang.faslbadmin.admin.service;

import com.zhangyh.FasLB.dto.FasUserQueryDto;
import com.zhang.faslbadmin.admin.model.vo.LoginVerifyImgResult;
import com.zhang.faslbadmin.admin.model.vo.PageInfo;
import com.zhangyh.FasLB.model.FasUserAccount;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

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
     FasUserAccount getAdminByUserAccount(String userAccount);

    /**
     * 查询所有用户
     * @return /
     */
     List<FasUserAccount> listAll();

    /**
     * 账号信息分页查询
     * @param userQueryDto /
     * @return /
     */
     PageInfo<FasUserAccount> pageList(FasUserQueryDto userQueryDto);

    /**
     * 获取验证码
     * @return /
     */
    LoginVerifyImgResult<byte[]> getVerifyCode();
}

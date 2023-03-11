package com.zhang.faslbadmin.common.config;

import com.zhang.faslbadmin.admin.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.annotation.Resource;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  20:10
 */
@Configuration
public class AdminSecurityConfig {

    @Lazy
    @Resource
    private UserService userService;

    @Bean
    public UserDetailsService userDetailsService(){
        //获取登录用户信息
        return userAccount -> userService.loadUserByUserAccount(userAccount);
    }
}

package com.zhangyh.security.config;

import com.zhangyh.security.handle.JwtAuthenticationTokenFilter;
import com.zhangyh.security.handle.RestAuthenticationEntryPoint;
import com.zhangyh.security.handle.RestfulAccessDeniedHandler;
import com.zhangyh.security.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/9  21:55
 */
@Configuration
public class CommonSecurityConfig {
    /**
     * 加密方式
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 白名单
     */
    @Bean
    public IgnoreUrlsConfig ignoreUrlsConfig() {
        return new IgnoreUrlsConfig();
    }


    @Bean
    public RestAuthenticationEntryPoint restAuthenticationEntryPoint(){
        return new RestAuthenticationEntryPoint();
    }

    @Bean
    public RestfulAccessDeniedHandler restfulAccessDeniedHandler(){
        return new RestfulAccessDeniedHandler();
    }

    /**
     * token工具
     */
    @Bean
    public JwtUtils jwtTokenUtil(){
        return new JwtUtils();
    }

    /**
     * token校验过滤器
     */
    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter(){
        return new JwtAuthenticationTokenFilter();
    }


    public static void main(String[] args) {
        final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        System.out.println(bCryptPasswordEncoder.encode("123456"));
    }
}

package com.zhangyh.security.config;

import com.zhangyh.security.handle.JwtAuthenticationTokenFilter;
import com.zhangyh.security.handle.RestAuthenticationEntryPoint;
import com.zhangyh.security.handle.RestfulAccessDeniedHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/9  21:53
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
public class SecurityConfig {

    private  final Logger log= LoggerFactory.getLogger(SecurityConfig.class);
    /**
     * 白名单
     */
    private final IgnoreUrlsConfig ignoreUrlsConfig;

    /**
     * 认证失败
     */
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    /**
     *  鉴权失败
     */
    private final RestfulAccessDeniedHandler restfulAccessDeniedHandler;


    /**
     *  Jwt检验过滤器
     */
    private final JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;


    @Autowired
    public SecurityConfig( IgnoreUrlsConfig ignoreUrlsConfig,  RestAuthenticationEntryPoint restAuthenticationEntryPoint, RestfulAccessDeniedHandler restfulAccessDeniedHandler, JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter) {
        this.ignoreUrlsConfig = ignoreUrlsConfig;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restfulAccessDeniedHandler = restfulAccessDeniedHandler;
        this.jwtAuthenticationTokenFilter = jwtAuthenticationTokenFilter;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry registry = http.authorizeRequests();
        //设置白名单
        for(String url: ignoreUrlsConfig.getUrls()){
            log.info("白名单：{}",url);
            registry.antMatchers(url).permitAll();
        }
        registry.antMatchers(HttpMethod.OPTIONS)
                .permitAll();

        registry.and()
                //因为使用token了所以关闭csrf
                .csrf().disable()
                // 不通过Session获取SecurityContext
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 允许跨域
                .cors()
                .and()
                // 配置路劲是否需要认证
                .authorizeRequests()
                // 除上面外的所有请求全部需要鉴权认证
                .anyRequest().authenticated()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(restfulAccessDeniedHandler)
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and().
                addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}


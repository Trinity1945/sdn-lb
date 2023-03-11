package com.zhang.faslbadmin.admin.service.impl;

import com.zhang.faslbadmin.admin.mapper.UserMapper;
import com.zhang.faslbadmin.admin.model.bo.AdminUserDetails;
import com.zhang.faslbadmin.admin.model.po.User;
import com.zhang.faslbadmin.admin.service.UserService;
import com.zhangyh.common.exception.Asserts;
import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.security.util.JwtUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  20:13
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    UserMapper userMapper;

    @Lazy
    @Resource
    private JwtUtils jwtTokenUtil;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public String login(String userAccount, String password) {
        String token = null;
        //数据校验
        if (StringUtils.isAnyBlank(userAccount, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        if (password.length() < 6) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        //账户校验不包含特殊字符
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码包含特殊字符");
        }
        try {
            UserDetails userDetails = loadUserByUserAccount(userAccount);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密码不正确");
            }
            if (!userDetails.isEnabled()) {
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录失败:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public UserDetails loadUserByUserAccount(String userAccount) {
        User admin = getAdminByUserAccount(userAccount);
        if (Optional.ofNullable(admin).isPresent()) {
            return new AdminUserDetails(admin);
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户不存在");
    }

    @Override
    public User getAdminByUserAccount(String userAccount) {
        //查数据库用户信息
        final Example example = new Example(User.class);
        example.createCriteria().andEqualTo("userAccount",userAccount);
        return userMapper.selectOneByExample(example);
    }
}

package com.zhang.faslbadmin.admin.service.impl;

import cn.hutool.core.util.IdUtil;
import com.github.pagehelper.PageHelper;
import com.zhang.faslbadmin.admin.model.bo.AdminUserDetails;
import com.zhang.faslbadmin.admin.model.vo.LoginVerifyImgResult;
import com.zhang.faslbadmin.admin.model.vo.PageInfo;
import com.zhang.faslbadmin.admin.service.UserService;
import com.zhang.faslbadmin.common.constant.RedisConstants;
import com.zhang.faslbadmin.common.util.CaptchaUtil;
import com.zhangyh.FasLB.dto.FasUserQueryDto;
import com.zhangyh.FasLB.mapper.FasUserAccountMapper;
import com.zhangyh.FasLB.model.FasUserAccount;
import com.zhangyh.common.exception.Asserts;
import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.common.util.RedisUtil;
import com.zhangyh.security.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  20:13
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    RedisUtil redisUtil;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FasUserAccountMapper userMapper;

    @Lazy
    @Resource
    private JwtUtils jwtTokenUtil;

    @Resource
    private BCryptPasswordEncoder passwordEncoder;

    @Resource
    private  AuthenticationManagerBuilder authenticationManagerBuilder;

    @Override
    public String login(String userAccount, String password) {
        String token = null;
        try {
            UserDetails userDetails = loadUserByUserAccount(userAccount);
            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                Asserts.fail("密码不正确");
            }
            if (!userDetails.isEnabled()) {
                Asserts.fail("帐号已被禁用");
            }
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userAccount, password);
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //方式二
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
            token = jwtTokenUtil.generateToken(userDetails);
        } catch (AuthenticationException e) {
            LOGGER.warn("登录失败:{}", e.getMessage());
        }
        return token;
    }

    @Override
    public UserDetails loadUserByUserAccount(String username) {
        FasUserAccount admin = getAdminByUserAccount(username);
        if (Optional.ofNullable(admin).isPresent()) {
            return new AdminUserDetails(admin);
        }
        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "用户不存在");
    }

    @Override
    public FasUserAccount getAdminByUserAccount(String username) {
        //查数据库用户信息
        final Example example = new Example(FasUserAccount.class);
        example.createCriteria().andEqualTo("username", username);
        return userMapper.selectOneByExample(example);
    }

    @Override
    public List<FasUserAccount> listAll() {
        return userMapper.selectAll();
    }

    @Override
    public PageInfo<FasUserAccount> pageList(FasUserQueryDto userQueryDto) {
        if (userQueryDto.getLimit() == null) {
            userQueryDto.setLimit(1);
        }
        if (userQueryDto.getPage() == null) {
            userQueryDto.setPage(10);
        }
        PageHelper.startPage(userQueryDto.getLimit(), userQueryDto.getPage());
        List<FasUserAccount> fasUsers = userMapper.selectAllAccount(userQueryDto);
        com.github.pagehelper.PageInfo<FasUserAccount> pageInfo = new com.github.pagehelper.PageInfo<>(fasUsers);
        PageInfo<FasUserAccount> userPage = new PageInfo<>();
        userPage.setPage(pageInfo.getPages());
        userPage.setData(fasUsers);
        userPage.setTotal(pageInfo.getTotal());
        return userPage;
    }

    @Override
    public LoginVerifyImgResult<byte[]> getVerifyCode() {
        String randomKey = IdUtil.randomUUID();
        String verifyCode = CaptchaUtil.verifyCode(4);
        byte[] imageCode = CaptchaUtil.createImageCode(verifyCode);
        redisUtil.expire(RedisConstants.VERIFY_CODE.getKey() + randomKey, 60, TimeUnit.SECONDS);
        LoginVerifyImgResult<byte[]> loginVerifyImgResult = new LoginVerifyImgResult<>();
        loginVerifyImgResult.setImgBase64(imageCode);
        loginVerifyImgResult.setKey(randomKey);
        return loginVerifyImgResult;
    }
}

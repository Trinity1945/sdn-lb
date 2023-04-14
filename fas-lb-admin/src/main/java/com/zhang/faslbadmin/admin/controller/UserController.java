package com.zhang.faslbadmin.admin.controller;

import com.zhang.faslbadmin.admin.model.bo.AdminUserDetails;
import com.zhang.faslbadmin.admin.model.dto.UserAccountDto;
import com.zhang.faslbadmin.admin.model.vo.LoginVerifyImgResult;
import com.zhang.faslbadmin.admin.model.vo.PageInfo;
import com.zhang.faslbadmin.admin.service.ImgVerifyCodeService;
import com.zhang.faslbadmin.admin.service.UserService;
import com.zhang.faslbadmin.common.valid.LoginGroup;
import com.zhangyh.FasLB.dto.FasUserQueryDto;
import com.zhangyh.FasLB.model.FasUserAccount;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.common.http.respose.BaseResponse;
import com.zhangyh.common.http.respose.ResponseHelper;
import com.zhangyh.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/9  19:45
 */
@Api(tags = "登录模块")
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${jwt.tokenHeader}")
    private String tokenHeader;

    @Value("${jwt.tokenHead}")
    private String tokenHead;

    @Resource
    UserService userService;

    @Resource
    ImgVerifyCodeService imgVerifyCodeService;

    @ApiOperation(value = "用户登录", httpMethod = "POST")
    @PostMapping("/login")
    public BaseResponse<Object> login(@Validated(LoginGroup.class) @RequestBody UserAccountDto user) {
        String verifyCode = user.getVerifyCode();
        String verifyCodeUUID = user.getVerifyCodeUUID();
        //验证验证码
        imgVerifyCodeService.checkCaptcha(verifyCodeUUID, verifyCode);
        String token = userService.login(user.getUserAccount(), user.getPassword());
        if (token == null) {
            return ResponseHelper.failed(ErrorCode.BUSINESS_ERROR);
        }
        Map<String, String> tokenMap = new HashMap<>(16);
        tokenMap.put(tokenHeader, token);
        tokenMap.put("tokenHead", tokenHead);
        return ResponseHelper.success(tokenMap);
    }

    @ApiOperation(value = "获取图形验证码", notes = "通过本地缓存实现")
    @GetMapping("/verifyCode")
    public BaseResponse<LoginVerifyImgResult<String>> verifyCode() {
        final LoginVerifyImgResult<String> loginVerifyImgResult = imgVerifyCodeService.generatorCaptcha();
        return ResponseHelper.success(loginVerifyImgResult);
    }

    @ApiOperation(value = "获取图形验证码2", notes = "通过redis缓存实现")
    @GetMapping("/verifyCode1")
    public BaseResponse<LoginVerifyImgResult<byte[]>> verifyCode1() {
        return ResponseHelper.success(userService.getVerifyCode());
    }

    @ApiOperation(value = "账号分页查询")
    @PostMapping("/pageList")
    public BaseResponse<PageInfo<FasUserAccount>> pageList(@RequestBody FasUserQueryDto fasUserQueryDto) {
        return ResponseHelper.success(userService.pageList(fasUserQueryDto));
    }

    @ApiOperation(value = "获取登录的用户")
    @GetMapping("/getCurrentUser")
    public BaseResponse<FasUserAccount> getCurrentUser() {
        AdminUserDetails currentUser = (AdminUserDetails) SecurityUtils.getCurrentUser();
        return ResponseHelper.success(currentUser.getUser());
    }

    @ApiOperation(value = "获取登录的用户名")
    @GetMapping("/getCurrentUserName")
    public BaseResponse<String> getCurrentUserName() {
        String currentUsername = SecurityUtils.getCurrentUsername();
        return ResponseHelper.success(currentUsername);
    }
}

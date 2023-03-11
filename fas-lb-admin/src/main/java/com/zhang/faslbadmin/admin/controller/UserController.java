package com.zhang.faslbadmin.admin.controller;

import com.zhang.faslbadmin.admin.model.dto.UserDto;
import com.zhang.faslbadmin.admin.service.UserService;
import com.zhang.faslbadmin.common.util.RequestHolder;
import com.zhang.faslbadmin.common.valid.LoginGroup;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.common.http.respose.BaseResponse;
import com.zhangyh.common.http.respose.ResponseHelper;
import com.zhangyh.logging.common.anotation.Log;
import com.zhangyh.logging.common.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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

    @ApiOperation(value = "登录",httpMethod = "POST")
    @PostMapping("/login")
    public BaseResponse<Object> login(@Validated(LoginGroup.class) @RequestBody UserDto user){
        String token = userService.login(user.getUserAccount(), user.getPassword());
        if(token==null){
            return ResponseHelper.failed(ErrorCode.BUSINESS_ERROR);
        }
        Map<String, String> tokenMap = new HashMap<>(16);
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return ResponseHelper.success(tokenMap);
    }

    @Log("测试日志")
    @GetMapping("/t")
    public String test(){

        final String localIp = StringUtils.getLocalIp();
        final String weekDay = StringUtils.getWeekDay();
        final HttpServletRequest request = RequestHolder.getHttpServletRequest();
        final String browser = StringUtils.getBrowser(request);
        final String ip = StringUtils.getIp(request);
        final String cityInfo = StringUtils.getCityInfo(ip);
        final String localCityInfo = StringUtils.getLocalCityInfo(localIp);
        log.info("localIp:{}--weekDay:{}--browser:{}--ip:{}--cityInfo:{}--localCityInfo:{}",localIp,weekDay,browser,ip,cityInfo,localCityInfo);

        return "success";
    }
}

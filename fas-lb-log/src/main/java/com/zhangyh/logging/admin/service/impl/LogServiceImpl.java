package com.zhangyh.logging.admin.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import com.zhangyh.logging.admin.mapper.LogMapper;
import com.zhangyh.logging.admin.model.po.Log;
import com.zhangyh.logging.admin.service.LogService;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/11  8:20
 */
@Service
public class LogServiceImpl implements LogService {

    @Resource
    LogMapper logMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, Log log) {
        if(log==null){
            throw new BusinessException(ErrorCode.MISSING_PARAMS);
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.zhangyh.logging.common.anotation.Log aopLog = method.getAnnotation(com.zhangyh.logging.common.anotation.Log.class);
        //方法路径
        final String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";
        log.setDescription(aopLog.value());
        log.setRequestIp(ip);
        log.setAddress("");
        log.setUsername(username);
        log.setParams(getParameter(method,joinPoint.getArgs()));
        // 记录登录用户，隐藏密码信息
        if(signature.getName().equals("login") && StringUtils.isNotEmpty(log.getParams())){
            JSONObject obj = JSONUtil.parseObj(log.getParams());
            log.setUsername(obj.getStr("username", ""));
            log.setParams(JSONUtil.toJsonStr(Dict.create().set("username", log.getUsername())));
        }
        log.setBrowser(browser);
        logMapper.insert(log);
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private String getParameter(Method method, Object[] args) {
        ArrayList<Object> argList = new ArrayList<>();
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i <parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if(requestBody!=null){
                argList.add(args[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if(requestParam!=null){
                HashMap<String, Object> map = new HashMap<>(2);
                String key=parameters[i].getName();
                if (!StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.isEmpty()) {
            return "";
        }
        return argList.size()==1? JSONUtil.toJsonStr(argList.get(0)) : JSONUtil.toJsonStr(argList);

    }
}

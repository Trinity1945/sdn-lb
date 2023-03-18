package com.zhangyh.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @Author: zhangyh
 * @desc 获取 HttpServletRequest
 * @date: 2023/3/18  8:59
 */
public class RequestHolder {

    public static HttpServletRequest getHttpServletRequest(){
        return ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
    }
}

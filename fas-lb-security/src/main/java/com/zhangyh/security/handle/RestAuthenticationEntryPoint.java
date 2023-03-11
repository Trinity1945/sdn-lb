package com.zhangyh.security.handle;

import cn.hutool.json.JSONUtil;
import com.zhangyh.common.http.respose.ResponseHelper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/9  22:16
 */
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control","no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println( JSONUtil.parse(ResponseHelper.failed(authException.getMessage())));
        response.getWriter().flush();
    }

}

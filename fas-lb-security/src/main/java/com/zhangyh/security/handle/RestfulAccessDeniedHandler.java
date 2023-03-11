package com.zhangyh.security.handle;

import cn.hutool.json.JSONUtil;
import com.zhangyh.common.http.respose.ResponseHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/9  22:17
 */
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {



    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Cache-Control","no-cache");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println(JSONUtil.parse(ResponseHelper.failed(e.getMessage())));
        response.getWriter().flush();
    }
}


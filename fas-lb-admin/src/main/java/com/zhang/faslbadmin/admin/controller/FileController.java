package com.zhang.faslbadmin.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/1  20:55
 */
@Slf4j
@RestController
public class FileController {

    @GetMapping("/file")
    public void getFileContent(HttpServletResponse response) throws IOException {
        response.setContentType("text/plain"); // 设置响应类型为文本
        response.setHeader("Content-Disposition", "attachment; filename=file.txt"); // 设置下载文件名
        log.info("文件上传");
        // 读取本地文件并将内容写入响应流
        InputStream is = new FileInputStream("C:\\Users\\17533\\Desktop\\t.txt");
        OutputStream os = response.getOutputStream();
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        os.flush();
        os.close();
        is.close();
    }
}
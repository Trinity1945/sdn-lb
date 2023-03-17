package com.zhang.faslbadmin.common.util;

import java.awt.*;
import java.security.SecureRandom;

/**
 * @Author: zhangyh
 * @desc 验证码工具类
 * @date: 2023/3/16  22:36
 */
public class CaptchaUtil {




    /**
     * 生成随机颜色
     * @param fc
     * @param bc
     * @return
     */
    public static Color getRandomColor(int fc,int bc){
        if(fc>255){
            fc=255;
        }
        if(bc>255){
            bc=255;
        }
        final SecureRandom secureRandom = new SecureRandom();
        int r=fc+secureRandom.nextInt(fc-bc);
        int g=fc+secureRandom.nextInt(fc-bc);
        int b=fc+secureRandom.nextInt(fc-bc);
        return new Color(r,g,b);
    }


    /**
     * 生成随机验证码
     * @return 验证码
     */
    public static String createVerifyCode(){
        SecureRandom secureRandom = new SecureRandom();
        final StringBuilder code = new StringBuilder();
        for(int i=0;i<4;i++){
            code.append(secureRandom.nextInt(10));
        }
        return code.toString();
    }
}

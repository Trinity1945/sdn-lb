package com.zhang.faslbadmin.common.util;

import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @Author: zhangyh
 * @desc 验证码工具类
 * @date: 2023/3/16  22:36
 */
public class CaptchaUtil {

    /**
     * 生成验证码
     * @param length 验证码长度
     * @return 验证码
     */
    public static String verifyCode(Integer length) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char element;
            int charOrNum = ThreadLocalRandom.current().nextInt(2);
            if ((charOrNum % 2) == 0) {
                element = (char) ThreadLocalRandom.current().nextInt(48, 58);
            }
            else {
                element = (char) ThreadLocalRandom.current().nextInt(97, 123);
                // 去除o和i
                while (element == 111 || element == 105) {
                    element = (char) ThreadLocalRandom.current().nextInt(97, 123);
                }
            }
            builder.append(element);
        }
        return builder.toString().toUpperCase();
    }

    public static byte[] createImageCode(String verifyCode){
        try {
            int width = 120;
            int height = 45;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            //创建Graphics对象，相当于画笔
            Graphics g = image.getGraphics();
            //创建
            Graphics2D graphics2D = (Graphics2D) g;
            Random random = new SecureRandom();
            g.setColor(getRandomColor(185, 250));
//            g.setColor(getRandomColor(135, 100));
            g.fillRect(0, 0, width, height);
            g.setFont(new Font("Times New Roman", Font.ITALIC, 35));
            g.setColor(getRandomColor(160, 200));
            for (int i = 0; i < 155; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int x1 = random.nextInt(12);
                int y1 = random.nextInt(12);
                BasicStroke bs = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
                Line2D line = new Line2D.Double(x, y, (double) x1 + x, (double) y1 + y);
                graphics2D.setStroke(bs);
                graphics2D.draw(line);
            }
            Color color = new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110));
            g.setColor(color);
            g.drawString(verifyCode, 19, 35);
            //直接在这里放入redis
            g.dispose();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, "JPEG", byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    private static Color getRandomColor(int fc,int bc){
        SecureRandom secureRandom = new SecureRandom();
        if(fc>255){
            fc=255;
        }
        if(bc>255){
            bc=255;
        }
        int r=fc+secureRandom.nextInt(bc-fc);
        int g=fc+secureRandom.nextInt(bc-fc);
        int b=fc+secureRandom.nextInt(bc-fc);
        return new Color(r,g,b);
    }
}

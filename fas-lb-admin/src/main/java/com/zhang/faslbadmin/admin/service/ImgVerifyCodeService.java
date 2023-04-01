package com.zhang.faslbadmin.admin.service;

import com.zhang.faslbadmin.admin.model.vo.LoginVerifyImgResult;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/19  9:53
 */
public interface ImgVerifyCodeService {

    /**
     * 生成验证码
     * @return
     */
    LoginVerifyImgResult<String> generatorCaptcha();

    /**
     * 对验证码进行验证.
     * @param uuid 验证码 uuid
     * @param code 验证码
     * @return
     */
    boolean verifyCaptcha(String uuid, String code);

    /**
     *  对验证码进行验证, 如验证失败则抛出异常
     * @param uuid
     * @param code
     */
    void checkCaptcha(String uuid, String code);
}

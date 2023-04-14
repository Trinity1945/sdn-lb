package com.zhang.faslbadmin.admin.service.impl;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.FIFOCache;
import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.CircleCaptcha;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.digest.DigestAlgorithm;
import cn.hutool.crypto.digest.Digester;
import com.zhang.faslbadmin.admin.model.vo.LoginVerifyImgResult;
import com.zhang.faslbadmin.admin.service.ImgVerifyCodeService;
import com.zhangyh.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/19  9:36
 */
@Service
public class ImgVerifyCodeServiceImpl implements ImgVerifyCodeService {

    /**
     * 最大容量为 100 的验证码缓存，防止恶意请求占满内存. 验证码有效期为 60 秒.
     */
    private final FIFOCache<String, String> verifyCodeCache = CacheUtil.newFIFOCache(100,60 * 1000L);

    @Override
    public LoginVerifyImgResult<String> generatorCaptcha() {
        CircleCaptcha captcha = CaptchaUtil.createCircleCaptcha(200, 45, 4, 7);
        String code = captcha.getCode();
        String imageBase64 = captcha.getImageBase64Data();

        String uuid = UUID.fastUUID().toString();
        Digester md5 = new Digester(DigestAlgorithm.MD5);
        String key = md5.digestHex(uuid);
        verifyCodeCache.put(key, code);

        LoginVerifyImgResult<String> loginVerifyImgResult = new LoginVerifyImgResult();
        loginVerifyImgResult.setImgBase64(imageBase64);
        loginVerifyImgResult.setKey(key);
        return loginVerifyImgResult;
    }

    @Override
    public boolean verifyCaptcha(String key, String code) {
        String expectedCode = verifyCodeCache.get(key);
        return Objects.equals(expectedCode, code);
    }

    @Override
    public void checkCaptcha(String key, String code) {
        boolean flag = verifyCaptcha(key, code);
        if (!flag) {
            throw new BusinessException("验证码错误或已失效");
        }
        //校验通过 删除验证码
        verifyCodeCache.remove(key);
    }
}

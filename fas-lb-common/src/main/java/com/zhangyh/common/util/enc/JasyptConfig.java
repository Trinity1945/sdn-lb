package com.zhangyh.common.util.enc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangyh
 * @Date 2023/2/28 14:20
 * @desc
 */

//@Configuration
public class JasyptConfig {

    //通过配置文件获取密钥
    @Value("${jasypt.encryptor.password:64EC7C763AB7BF64E2D75FF83A319918}")
    private String password;

    @Bean("SM4ECBStringEncryptor")
    public ZhdEncryptor zhdEncryptor(){
        //通过参数获取密钥
//        String secretKey = System.getProperty("secretKey");
        return new SingleEncryption(password);
    }

    @Value("${rsa.public-key}")
    private String public_key;
    @Value("${rsa.private-key}")
    private String private_key;

    @Bean("RsaStringEncryptor")
    public ZhdEncryptor RsaStringEncryptor(){
        return new RsaStringEncryptor(public_key,private_key);
    }

}

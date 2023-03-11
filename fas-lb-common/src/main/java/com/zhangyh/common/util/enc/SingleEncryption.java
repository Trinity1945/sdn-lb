package com.zhangyh.common.util.enc;

import lombok.extern.slf4j.Slf4j;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;

/**
 * @author zhangyh
 * @Date 2023/2/28 14:11
 * @desc
 */

@Slf4j
public class SingleEncryption implements ZhdEncryptor{

    /**
     * 密钥
     */
    private final String password;

    /**
     * 加密算法
     */
    private static String Algorithm=StandardPBEByteEncryptor.DEFAULT_ALGORITHM;

    public SingleEncryption(String password) {
        this.password=password;
    }


    public SingleEncryption(String password, String algorithm) {
        this.password = password;
        Algorithm = algorithm;
    }

    @Override
    public String encrypt(String msg) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(password));
        return encryptor.encrypt(msg);
    }

    @Override
    public String decrypt(String msg) {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        encryptor.setConfig(cryptor(password));
        return encryptor.decrypt(msg);
    }

    /**
     * config配置
     * @param key 密钥
     */
    public static SimpleStringPBEConfig cryptor(String key){
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(key);
        config.setAlgorithm(StandardPBEByteEncryptor.DEFAULT_ALGORITHM.equals(Algorithm)?StandardPBEByteEncryptor.DEFAULT_ALGORITHM:Algorithm);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName(null);
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        return config;
    }
}

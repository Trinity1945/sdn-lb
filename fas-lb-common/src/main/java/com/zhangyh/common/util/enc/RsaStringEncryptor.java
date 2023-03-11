package com.zhangyh.common.util.enc;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author zhangyh
 * @Date 2023/2/28 15:12
 * @desc
 */
@Slf4j
public class RsaStringEncryptor implements ZhdEncryptor{

    /**
     * 公钥
     */
    private   String publicKey;

    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 私钥
     */
    private   String privateKey;



    public RsaStringEncryptor(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }


    public static PublicKey getPublicKey(String publicKeyString) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] publicKeyByte = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePublic(keySpec);
    }


    public static PrivateKey getPrivateKey(String privateKeyString) throws Exception {
        byte[] privateKeyByte = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyByte);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return keyFactory.generatePrivate(keySpec);
    }


    @Override
    public String encrypt(String msg) {
        try {
            log.info("明文字符串为:[{}]", msg);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
            byte[] tempBytes = cipher.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(tempBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密字符串[" + msg + "]时遇到异常", e);
        }
    }

    @Override
    public String decrypt(String msg) {
        try {
            // 生成私钥
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
            // 密文解码
            byte[] secretTextDecoded = Base64.getDecoder().decode(msg.getBytes(StandardCharsets.UTF_8));
            byte[] tempBytes = cipher.doFinal(secretTextDecoded);
            return new String(tempBytes);
        } catch (Exception e) {
            throw new RuntimeException("解密字符串[" + msg + "]时遇到异常", e);
        }
    }
}

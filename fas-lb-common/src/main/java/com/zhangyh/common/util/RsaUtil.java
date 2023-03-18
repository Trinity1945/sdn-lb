package com.zhangyh.common.util;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/18  9:02
 */
public class RsaUtil {

    private static final String SRC = "123456";

    /**
     * 生成密钥对
     * @return 密钥实体
     */
    public static RsaKeyPair generateKeyPair() throws NoSuchAlgorithmException {
        final KeyPairGenerator rsa = KeyPairGenerator.getInstance("RSA");
        //初始化密钥长度
        rsa.initialize(1024);
        final KeyPair keyPair = rsa.generateKeyPair();
        final RSAPublicKey publicKey =(RSAPublicKey) keyPair.getPublic();
        final RSAPrivateKey privateKey =(RSAPrivateKey) keyPair.getPrivate();
        final String publicKeyString = Base64.encodeBase64String(publicKey.getEncoded());
        final String privateKeyString = Base64.encodeBase64String(privateKey.getEncoded());
        return new RsaKeyPair(publicKeyString,privateKeyString);
    }

    /**
     * 公钥加密
     * @param publicKey 公钥
     * @param context 加密内容
     * @return 加密后的内容
     */
    public static String encryptByPublicKey(String publicKey,String context) throws Exception {
        final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PublicKey publicKey1 = keyFactory.generatePublic(x509EncodedKeySpec);
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey1);
        byte[] result = doLongerCipherFinal(Cipher.ENCRYPT_MODE, cipher, context.getBytes());
        return Base64.encodeBase64String(result);
    }


    private static byte[] doLongerCipherFinal(int opMode,Cipher cipher, byte[] source) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (opMode == Cipher.DECRYPT_MODE) {
            out.write(cipher.doFinal(source));
        } else {
            int offset = 0;
            int totalSize = source.length;
            while (totalSize - offset > 0) {
                int size = Math.min(cipher.getOutputSize(0) - 11, totalSize - offset);
                out.write(cipher.doFinal(source, offset, size));
                offset += size;
            }
        }
        out.close();
        return out.toByteArray();
    }

    /**
     * 私钥解密
     * @param privateKey 私钥
     * @param context 需解密的内容
     * @return 解密后的内容
     */
    public static String decryptByPrivateKey(String privateKey,String context) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey pKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pKey);
        byte[] result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, Base64.decodeBase64(context));
        return new String(result);
    }

    /**
     * 公钥解密
     *
     * @param publicKeyText 公钥
     * @param text 待解密的信息
     * @return /
     * @throws Exception /
     */
    public static String decryptByPublicKey(String publicKeyText, String text) throws Exception {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, Base64.decodeBase64(text));
        return new String(result);
    }

    /**
     * 私钥加密
     *
     * @param privateKeyText 私钥
     * @param text 待加密的信息
     * @return /
     * @throws Exception /
     */
    public static String encryptByPrivateKey(String privateKeyText, String text) throws Exception {
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = doLongerCipherFinal(Cipher.ENCRYPT_MODE, cipher, text.getBytes());
        return Base64.encodeBase64String(result);
    }


    /**
     * RSA密钥对象
     */
    @Data
    public static class RsaKeyPair{
        private String publicKey;
        private String privateKey;

        public RsaKeyPair(String publicKey, String privateKey) {
            this.publicKey = publicKey;
            this.privateKey = privateKey;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("\n");
        RsaKeyPair keyPair = generateKeyPair();
        System.out.println("公钥：" + keyPair.getPublicKey());
        System.out.println("私钥：" + keyPair.getPrivateKey());
        System.out.println("\n");
        test1(keyPair);
        System.out.println("\n");
        test2(keyPair);
        System.out.println("\n");
    }

    /**
     * 公钥加密私钥解密
     */
    private static void test1(RsaKeyPair keyPair) throws Exception {
        System.out.println("***************** 公钥加密私钥解密开始 *****************");
        String text1 = encryptByPublicKey(keyPair.getPublicKey(), RsaUtil.SRC);
        String text2 = decryptByPrivateKey(keyPair.getPrivateKey(), text1);
        System.out.println("加密前：" + RsaUtil.SRC);
        System.out.println("加密后：" + text1);
        System.out.println("解密后：" + text2);
        if (RsaUtil.SRC.equals(text2)) {
            System.out.println("解密字符串和原始字符串一致，解密成功");
        } else {
            System.out.println("解密字符串和原始字符串不一致，解密失败");
        }
        System.out.println("***************** 公钥加密私钥解密结束 *****************");
    }

    /**
     * 私钥加密公钥解密
     * @throws Exception /
     */
    private static void test2(RsaKeyPair keyPair) throws Exception {
        System.out.println("***************** 私钥加密公钥解密开始 *****************");
        String text1 = encryptByPrivateKey(keyPair.getPrivateKey(), RsaUtil.SRC);
        String text2 = decryptByPublicKey(keyPair.getPublicKey(), text1);
        System.out.println("加密前：" + RsaUtil.SRC);
        System.out.println("加密后：" + text1);
        System.out.println("解密后：" + text2);
        if (RsaUtil.SRC.equals(text2)) {
            System.out.println("解密字符串和原始字符串一致，解密成功");
        } else {
            System.out.println("解密字符串和原始字符串不一致，解密失败");
        }
        System.out.println("***************** 私钥加密公钥解密结束 *****************");
    }
}

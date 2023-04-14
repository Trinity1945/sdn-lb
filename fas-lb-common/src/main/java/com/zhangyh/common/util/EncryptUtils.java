package com.zhangyh.common.util;

import cn.hutool.core.codec.Base64;
import lombok.Data;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/18  9:02
 */
public class EncryptUtils {

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
        final String publicKeyString = cn.hutool.core.codec.Base64.encode(publicKey.getEncoded());
        final String privateKeyString = cn.hutool.core.codec.Base64.encode(privateKey.getEncoded());
        return new RsaKeyPair(publicKeyString,privateKeyString);
    }

    /**
     * 公钥加密
     * @param publicKey 公钥
     * @param context 加密内容
     * @return 加密后的内容
     */
    public static String encryptByPublicKey(String publicKey,String context) throws Exception {
        final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(cn.hutool.core.codec.Base64.decode(publicKey));
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final PublicKey publicKey1 = keyFactory.generatePublic(x509EncodedKeySpec);
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE,publicKey1);
        byte[] result = doLongerCipherFinal(Cipher.ENCRYPT_MODE, cipher, context.getBytes());
        return cn.hutool.core.codec.Base64.encode(result);
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
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(cn.hutool.core.codec.Base64.decode(privateKey));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey pKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, pKey);
        byte[] result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, cn.hutool.core.codec.Base64.decode(context));
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
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(cn.hutool.core.codec.Base64.decode(publicKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        byte[] result = doLongerCipherFinal(Cipher.DECRYPT_MODE, cipher, cn.hutool.core.codec.Base64.decode(text));
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
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(cn.hutool.core.codec.Base64.decode(privateKeyText));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        byte[] result = doLongerCipherFinal(Cipher.ENCRYPT_MODE, cipher, text.getBytes());
        return cn.hutool.core.codec.Base64.encode(result);
    }

    public static void der2pem(byte[] derBytes,String header,String fileName){
        String privateKeyPEM = byteToPEM(derBytes,header );
        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write(privateKeyPEM);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 将字节数组编码为 PEM 格式的字符串。
     *
     * @param bytes  字节数组
     * @param header 标头字符串（如 "PRIVATE KEY" 或 "PUBLIC KEY"）
     * @return PEM 格式的字符串
     */
    private static String byteToPEM(byte[] bytes, String header) {
        // 将字节数组编码为 Base64 格式的字符串，然后按 64 字节进行分片
        String base64String = cn.hutool.core.codec.Base64.encode(bytes);
        int chunkSize = 64;
        int totalChunks = (base64String.length() + chunkSize - 1) / chunkSize;

        // 拼接 PEM 格式的字符串
        StringBuilder sb = new StringBuilder();
        sb.append("-----BEGIN ").append(header).append("-----").append(LINE_SEPARATOR);
        for (int i = 0; i < totalChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(base64String.length(), (i + 1) * chunkSize);
            sb.append(base64String, start, end).append(LINE_SEPARATOR);
        }
        sb.append("-----END ").append(header).append("-----").append(LINE_SEPARATOR);

        return sb.toString();
    }

    /**
     * 从 PEM 文件中读取 RSA 密钥（私钥或公钥）。
     *
     * @param filename PEM 文件名
     * @param algorithm 密钥算法（如 "RSA"）
     * @param isPrivateKey 是否为私钥
     * @return RSA 密钥（私钥或公钥）
     */
    public static Object get(String filename, String algorithm, boolean isPrivateKey, String header)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        // 解析出 Base64 编码的 DER 格式字节串
        String keyBeginMarker= MessageFormat.format("-----BEGIN {0}-----",header);
        String keyEndMarker=MessageFormat.format("-----END {0}-----",header);
        String pemString = new String(keyBytes);
        int beginIndex = pemString.indexOf(keyBeginMarker) + keyBeginMarker.length();
        int endIndex = pemString.indexOf(keyEndMarker);
        String base64String = pemString.substring(beginIndex, endIndex);
        byte[] derBytes = cn.hutool.core.codec.Base64.decode(base64String);

        // 从 DER 格式字节串中解析出 RSA 密钥（私钥或公钥）
        KeyFactory kf = KeyFactory.getInstance(algorithm);
        if (isPrivateKey) {
            PKCS8EncodedKeySpec pkcs8 = new PKCS8EncodedKeySpec(derBytes);
            return kf.generatePrivate(pkcs8);
        } else {
            X509EncodedKeySpec x509 = new X509EncodedKeySpec(derBytes);
            return kf.generatePublic(x509);
        }
    }

    /**
     * 从 PEM 文件中读取 RSA 私钥。
     *
     * @param filename PEM 文件名
     * @return RSA 私钥
     */
    public static PrivateKey get(String filename)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(filename));

        // 解析出 Base64 编码的 DER 格式字节串
        String beginMarker = "-----BEGIN PRIVATE KEY-----";
        String endMarker = "-----END PRIVATE KEY-----";
        String pemString = new String(keyBytes);
        int beginIndex = pemString.indexOf(beginMarker) + beginMarker.length();
        int endIndex = pemString.indexOf(endMarker);
        String base64String = pemString.substring(beginIndex, endIndex);
        byte[] derBytes = cn.hutool.core.codec.Base64.decode(base64String);

        // 从 DER 格式字节串中解析出 RSA 私钥
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(derBytes);
        return kf.generatePrivate(keySpec);
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
//        保存公钥私钥
        String privateKeyHeader="PRIVATE KEY";
        String publicKeyHeader="PUBLIC KEY";
        String privateKeyFile="server.key";
        String publicKeyFIle="pub.key";
        der2pem(cn.hutool.core.codec.Base64.decode(keyPair.getPrivateKey()),privateKeyHeader,privateKeyFile);
        der2pem(cn.hutool.core.codec.Base64.decode(keyPair.getPublicKey()),publicKeyHeader,publicKeyFIle);
//        PrivateKey privateKey = get("server.key");
        PrivateKey privateKey = (PrivateKey) get("server.key", "RSA", true,privateKeyHeader);
        PublicKey pub = (PublicKey) get("pub.key", "RSA", false,publicKeyHeader);
        System.out.println(cn.hutool.core.codec.Base64.encode(privateKey.getEncoded()).equals(keyPair.getPrivateKey()));
        System.out.println(Base64.encode(pub.getEncoded()).equals(keyPair.getPublicKey()));
    }

    /**
     * 公钥加密私钥解密
     */
    private static void test1(RsaKeyPair keyPair) throws Exception {
        System.out.println("***************** 公钥加密私钥解密开始 *****************");
        String text1 = encryptByPublicKey(keyPair.getPublicKey(), EncryptUtils.SRC);
        String text2 = decryptByPrivateKey(keyPair.getPrivateKey(), text1);
        System.out.println("加密前：" + EncryptUtils.SRC);
        System.out.println("加密后：" + text1);
        System.out.println("解密后：" + text2);
        if (EncryptUtils.SRC.equals(text2)) {
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
        String text1 = encryptByPrivateKey(keyPair.getPrivateKey(), EncryptUtils.SRC);
        String text2 = decryptByPublicKey(keyPair.getPublicKey(), text1);
        System.out.println("加密前：" + EncryptUtils.SRC);
        System.out.println("加密后：" + text1);
        System.out.println("解密后：" + text2);
        if (EncryptUtils.SRC.equals(text2)) {
            System.out.println("解密字符串和原始字符串一致，解密成功");
        } else {
            System.out.println("解密字符串和原始字符串不一致，解密失败");
        }
        System.out.println("***************** 私钥加密公钥解密结束 *****************");
    }

}
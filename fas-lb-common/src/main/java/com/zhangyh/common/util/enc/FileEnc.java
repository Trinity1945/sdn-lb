package com.zhangyh.common.util.enc;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * @author zhangyh
 * @Date 2023/3/1 9:09
 * @desc 配置文件在需加密的数据添加前后缀自动生成加密后的配置文件 eg:ENC(需加密的数据)====》ENC(p9PkhAsmS1pkz23kqSVLrw==)-----------使用策略设计模式
 */
@Slf4j
public class FileEnc {

    /**
     * 不同加密策略
     */
    private final ZhdEncryptor encryptor;

    public FileEnc(ZhdEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    /**
     * 生成加密后的配置文件
     * @param originPath 源配置文件
     * @param targetPath 目标文件路径
     * @param prefix 加密前缀，同Jasypt中的前缀
     * @param suffix 加密后缀，同Jasypt中的后缀
     */
    public  void generateEncFile(String originPath, String targetPath,String prefix,String suffix) {
        Path target = Paths.get(targetPath);
        Path origin = Paths.get(originPath);
        try (
                BufferedReader in = Files.newBufferedReader(origin, StandardCharsets.UTF_8);
                BufferedWriter out = Files.newBufferedWriter(target,StandardCharsets.UTF_8)
        ) {
            if (Files.notExists(target)) {
                Files.createFile(target);
            }
            String line=null;
            while((line=in.readLine())!=null){
                //需加密数据 eg:ENC(root)
                int preIndex = line.indexOf(prefix);
                int sufIndex = line.indexOf(suffix);
                if(preIndex!=-1&&sufIndex!=-1&&sufIndex>preIndex){
                    String msg = line.substring(preIndex+prefix.length(), sufIndex);
                    String encMsg = encryptor.encrypt(msg);
                    line= line.replace(msg, encMsg);
                }
                out.write(line+ "\r\n");
            }
        } catch (IOException e) {
            log.error("配置文件加密失败");
            e.printStackTrace();
        }
    }

    /**
     * 文件拷贝
     * @param originPath 源文件路径
     * @param targetPath 目标输出文件路径
     */
    public void copyPropertiesFile(String originPath,String targetPath){
        Path origin = Paths.get(originPath);
        Path target = Paths.get(targetPath);
        try{
            Files.copy(origin,target, StandardCopyOption.REPLACE_EXISTING);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SingleEncryption pooledPBEtStringEncryptor = new SingleEncryption("64EC7C763AB7BF64E2D75FF83A319918");
        FileEnc fileEncUtil = new FileEnc(pooledPBEtStringEncryptor);
//        fileEncUtil.copyPropertiesFile("D:\\ideaProject\\User-center\\src\\main\\resources\\application.yml","\"D:\\\\ideaProject\\\\User-center\\\\src\\\\main\\\\resources\\\\application1.yml\"");
        fileEncUtil.generateEncFile("D:\\ideaProject\\User-center\\src\\main\\resources\\application1.yml","D:\\ideaProject\\User-center\\src\\main\\resources\\application.yml","ENC(",")");

    }
}

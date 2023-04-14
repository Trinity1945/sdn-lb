package com.zhang.faslbadmin.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangyh
 * @Date 2023/4/14 16:02
 * @desc
 */
@Configuration
public class RasConfig {

    @Value("${rsa.privateKeyHeader:PRIVATE KEY}")
    public   String privateKeyHeader="PRIVATE KEY";

    @Value("${rsa.publicKeyHeader:PUBLIC KEY}")
    public   String publicKeyHeader="PUBLIC KEY";

    @Value("${rsa.privateKeyFile:server.key}")
    public   String privateKeyFile="server.key";

    @Value("${rsa.publicKeyFIle:pub.key}")
    public   String publicKeyFIle="pub.key";

    @Value("${rsa.algorithm:RSA}")
    public  String algorithm="RSA";
}

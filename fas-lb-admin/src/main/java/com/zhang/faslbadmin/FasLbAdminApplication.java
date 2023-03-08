package com.zhang.faslbadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zhangyh
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FasLbAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FasLbAdminApplication.class, args);
    }

}

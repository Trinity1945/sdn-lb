package com.example.faslbloadbalancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author zhangyh
 */
@EnableDiscoveryClient
@SpringBootApplication
public class FasLbLoadbalancerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FasLbLoadbalancerApplication.class, args);
    }

}

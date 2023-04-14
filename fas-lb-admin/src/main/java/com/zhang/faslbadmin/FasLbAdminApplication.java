package com.zhang.faslbadmin;

import com.zhangyh.FasLB.sync.annotation.EnableTableSync;
import com.zhangyh.common.util.ipUtils.SpringContextHolder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author zhangyh
 */
@EnableTableSync("com.zhangyh.FasLB.model")
@EnableAsync
@EnableTransactionManagement
@MapperScan(basePackages = {"com.zhangyh.FasLB.mapper"})
@EnableDiscoveryClient
@SpringBootApplication
public class FasLbAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FasLbAdminApplication.class, args);
    }
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }
}

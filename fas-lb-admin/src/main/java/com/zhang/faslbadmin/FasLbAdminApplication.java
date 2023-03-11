package com.zhang.faslbadmin;

import com.zhangyh.logging.common.util.SpringContextHolder;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author zhangyh
 */
@EnableTransactionManagement
@MapperScan(basePackages = {"com.zhang.faslbadmin.admin.mapper","com.zhangyh.logging.admin.mapper"})
@EnableDiscoveryClient
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class FasLbAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(FasLbAdminApplication.class, args);
    }
    @Bean
    public SpringContextHolder springContextHolder() {
        return new SpringContextHolder();
    }


}

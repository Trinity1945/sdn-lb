package com.zhang.faslbadmin.common.config;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import io.swagger.annotations.ApiOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  21:41
 */
@Configuration
@EnableSwagger2
@EnableKnife4j
public class Knife4jConfiguration {
    @Bean
    public Docket defaultApi2() {
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.any())
                .build();

    }

    /**
     * 设置文档信息主页的内容说明
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("LiLoadbalance API后台")
                .description("LiLoadbalance-admin模块API")
                // 服务Url（网站地址）
                .termsOfServiceUrl("http://localhost:9000/")
                .contact(new Contact("zhangyh", null, "mc1753343931"))
                .version("1.0")
                .build();
    }
}

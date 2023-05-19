package com.example.faslbloadbalancer.common.config;

import com.example.faslbloadbalancer.common.util.ReactorWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/4/15  8:56
 */
@Configuration
public class WenClientConfig {

    @Value("${webclient.baseurl}")
    private String baseUrl;


    @Bean
    public WebClient webClient(){
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    @Bean
    public ReactorWebClient reactorWebClient(){
        return new ReactorWebClient(webClient());
    }
}

package com.example.faslbloadbalancer.common.config;

import com.example.faslbloadbalancer.common.http.response.GlobalResponseBodyHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:15
 */
@Configuration
public class WebFluxConfiguration implements WebFluxConfigurer {

    /**
     * 统一返回bean
     * @param serverCodecConfigurer -
     * @param requestedContentTypeResolver -
     * @return -
     */
    @Bean
    public GlobalResponseBodyHandler responseWrapper(ServerCodecConfigurer serverCodecConfigurer,
                                                     RequestedContentTypeResolver requestedContentTypeResolver) {
        return new GlobalResponseBodyHandler(serverCodecConfigurer.getWriters(), requestedContentTypeResolver);
    }
}

package com.zhangyh.faslbgateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: zhangyh
 * @desc 全局网关过滤器
 * @date: 2023/3/10  23:03
 */
@Slf4j
@Configuration
public class AccessGatewayFilter implements GlobalFilter, Ordered {
    /**
     * 处理当前请求,有必要的话通过{@link GatewayFilterChain}将请求交给下一个过滤器处理
     *
     * @param exchange 请求上下文，里面可以获取Request、Response等信息
     * @param chain    用来把请求委托给下一个过滤器
     * @return 返回标示当前过滤器业务结束
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("---------网关全局过滤器------");
        return chain.filter(exchange);
    }

    /**
     * 设置过滤器优先级，值越小优先级越高
     */
    @Override
    public int getOrder() {
        return 0;
    }
}

package com.example.faslbloadbalancer.common.util.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:17
 */
@Slf4j
@Component
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class ReactiveRequestContextFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("---过滤器处理---");
        return chain.filter(exchange)
                .contextWrite(context -> context.put(ReactiveHttpContextHolder.Info.CONTEXT_KEY, exchange));
    }
}

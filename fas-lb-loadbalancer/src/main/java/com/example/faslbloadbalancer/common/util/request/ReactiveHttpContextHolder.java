package com.example.faslbloadbalancer.common.util.request;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:18
 */
public class ReactiveHttpContextHolder {
    /**
     * 获取当前请求对象
     * @return ServerHttpRequest
     */
    public static Mono<ServerHttpRequest> getRequest() {
        return Mono.deferContextual(context ->Mono.justOrEmpty( context.get(Info.CONTEXT_KEY).getRequest()));
    }

    /**
     * 获取当前response
     * @return  ServerHttpResponse
     */
    public static Mono<ServerHttpResponse> getResponse(){
        return Mono.deferContextual(context -> Mono.justOrEmpty(context.get(Info.CONTEXT_KEY).getResponse()));
    }

    public static final class Info{
        public static final Class<ServerWebExchange> CONTEXT_KEY = ServerWebExchange.class;
    }
}

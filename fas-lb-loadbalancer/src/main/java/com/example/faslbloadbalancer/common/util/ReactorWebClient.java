package com.example.faslbloadbalancer.common.util;

import com.zhangyh.common.exception.BusinessException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author zhangyh
 * @Date 2023/4/13 17:28
 * @desc
 */

public class ReactorWebClient {

    @Resource
    private WebClient webClient;


    public ReactorWebClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public ReactorWebClient() {
    }

    public   <T> Mono<T> getMono(String url, Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(throwable -> new BusinessException("Error sending request: " + throwable.getMessage()));
    }

    public <T> Mono<T> getMono(String url, ParameterizedTypeReference<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(throwable -> new BusinessException("Error sending request: " + throwable.getMessage()));
    }

    public   <T> Flux<T> getFlux(String url, Class<T> responseType) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToFlux(responseType)
                .onErrorMap(throwable -> new BusinessException("Error sending request: " + throwable.getMessage()));
    }

    public <T> Mono<T> post(String url, Object requestBody,MediaType mediaType, Class<T> responseType) {
        return webClient.post()
                .uri(url)
                .contentType(mediaType==null?MediaType.APPLICATION_JSON:mediaType)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(throwable -> new BusinessException("Error sending request: " + throwable.getMessage()));
    }

    public <T> Mono<T> put(String url, Object requestBody, Class<T> responseType) {
        return webClient.put()
                .uri(url)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(throwable -> new BusinessException("Error sending request: " + throwable.getMessage()));
    }

    public <T> Mono<T> delete(String url, Class<T> responseType) {
        return webClient.delete()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .onErrorMap(throwable -> new BusinessException("Error sending request: " + throwable.getMessage()));
    }
}

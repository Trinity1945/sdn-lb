package com.example.faslbloadbalancer.common.http.response;

import com.zhangyh.common.http.respose.BaseResponse;
import com.zhangyh.common.http.respose.ResponseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

/**
 * @Author: zhangyh
 * @desc 统一处理响应式返回
 * @date: 2023/3/10  22:16
 */
public class GlobalResponseBodyHandler extends ResponseBodyResultHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalResponseBodyHandler.class);
    private static final MethodParameter METHOD_PARAMETER_MONO_COMMON_RESULT;

    private static final BaseResponse<Object> COMMON_RESULT_SUCCESS = ResponseHelper.success(null);

    static {
        try {
            // 获得 METHOD_PARAMETER_MONO_COMMON_RESULT 。其中 -1 表示 `#methodForParams()` 方法的返回值
            METHOD_PARAMETER_MONO_COMMON_RESULT = new MethodParameter(
                    GlobalResponseBodyHandler.class.getDeclaredMethod("methodForParams"), -1);
        } catch (NoSuchMethodException e) {
            LOGGER.error("[static][获取 METHOD_PARAMETER_MONO_COMMON_RESULT 时，找不都方法");
            throw new RuntimeException(e);
        }
    }

    public GlobalResponseBodyHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
        super(writers, resolver);
    }

    private static Mono<BaseResponse<Object>> methodForParams() {
        return null;
    }

    @Override
    public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
        Object returnValue = result.getReturnValue();
        Object body;
        if(returnValue instanceof Mono){
            body=((Mono<Object>) result.getReturnValue())
                    .map((Function<Object, Object>) GlobalResponseBodyHandler::wrapCommonResult)
                    .defaultIfEmpty(COMMON_RESULT_SUCCESS);;
        } else if (returnValue instanceof Flux) {
            body = ((Flux<Object>) result.getReturnValue())
                    .collectList()
                    .map((Function<Object, Object>) GlobalResponseBodyHandler::wrapCommonResult)
                    .defaultIfEmpty(COMMON_RESULT_SUCCESS);;
        } else {
            body = wrapCommonResult(returnValue);
        }
        return writeBody(body, METHOD_PARAMETER_MONO_COMMON_RESULT, exchange);
    }

    private static BaseResponse<?> wrapCommonResult(Object body) {
        // 如果已经是 BaseResponse 类型，则直接返回
        if (body instanceof BaseResponse) {
            return (BaseResponse<?>) body;
        }
        // 如果不是，则包装成 BaseResponse 类型
        return ResponseHelper.success(body);
    }
}

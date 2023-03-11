package com.zhangyh.logging.common.aspect;

import com.zhangyh.logging.admin.model.po.Log;
import com.zhangyh.logging.admin.service.LogService;
import com.zhangyh.logging.common.util.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:45
 */
@Slf4j
@Component
@Aspect
public class LogInterceptor {

    ThreadLocal<Long> currentTime = new ThreadLocal<>();
    @Resource
    LogService logService;

    @Pointcut("@annotation(com.zhangyh.logging.common.anotation.Log)")
    public void logPointcut(){
        //本方法无方法体，作用是给同类中其他方法使用此切入点
    }

    @Around("logPointcut()")
    public Object logAround(ProceedingJoinPoint point) throws Throwable {
        //计时
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = point.proceed();
        stopWatch.stop();
        //生成请求唯一ID
        String requestId = UUID.randomUUID().toString();
        //执行耗时
        long totalTimeMillis = stopWatch.getTotalTimeMillis();
        log.info("request end, id: {}, cost: {}ms", requestId, totalTimeMillis);
        // 获取请求路径
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest httpServletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();

        String url = httpServletRequest.getRequestURI();
        // 获取请求参数
        Object[] args = point.getArgs();
        String reqParam = "[" + StringUtils.join(args, ", ") + "]";
        // 输出请求日志
        log.info("request start，id: {}, path: {}, ip: {}, params: {}", requestId, url,
                httpServletRequest.getRemoteHost(), reqParam);
        Log log = new Log("INFO",totalTimeMillis);
//        logService.save(getUsername(),StringUtils.getBrowser(request),StringUtils.getIp(request),point, log);
        return result;
    }

    /**
     * 配置异常通知
     * @param joinPoint /
     * @param e /
     */
    @AfterThrowing(pointcut = "logPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        Log log = new Log("ERROR",System.currentTimeMillis() - currentTime.get());
        currentTime.remove();
        log.setExceptionDetail(ThrowableUtil.getStackTrace(e).getBytes());
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
//        logService.save(getUsername(), StringUtils.getBrowser(request), StringUtils.getIp(request), (ProceedingJoinPoint)joinPoint, log);
    }

    public String getUsername() {
        try {
            return "username";
        }catch (Exception e){
            return "";
        }
    }

}

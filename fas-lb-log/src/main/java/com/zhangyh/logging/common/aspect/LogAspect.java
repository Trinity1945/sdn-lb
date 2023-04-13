package com.zhangyh.logging.common.aspect;

import cn.hutool.json.JSONUtil;
import com.zhangyh.common.util.ipUtils.StringUtils;
import com.zhangyh.FasLB.model.Log;
import com.zhangyh.logging.common.config.EventPubListener;
import com.zhangyh.logging.common.util.ThrowableUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:45
 */
@Slf4j
@Component
@Aspect
public class LogAspect {

    @Resource
    private EventPubListener eventPubListener;

    ThreadLocal<Long> currentTime = new ThreadLocal<>();

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
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        final String browser = StringUtils.getBrowser(request);
        final String ip = StringUtils.getIp(request);
        final MethodSignature signature =  (MethodSignature)point.getSignature();
        Method method = signature.getMethod();
        com.zhangyh.logging.common.anotation.Log aopLog = method.getAnnotation(com.zhangyh.logging.common.anotation.Log.class);
        String methodName = point.getTarget().getClass().getName() + "." + signature.getName() + "()";
        final String parameter = getParameter(method, point.getArgs());
        Log log = new Log("INFO",totalTimeMillis);
        log.setMethod(methodName);
        log.setParams(parameter);
        log.setBrowser(browser);
        log.setRequestIp(ip);
        log.setDescription(aopLog.value());
        log.setUserAccount(getUsername());
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        eventPubListener.pushListener(log);
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
        final String browser = StringUtils.getBrowser(request);
        final String ip = StringUtils.getIp(request);
        log.setBrowser(browser);
        log.setRequestIp(ip);
        log.setUserAccount(getUsername());
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        eventPubListener.pushListener(log);
    }

    public String getUsername() {
        try {
            return "username";
        }catch (Exception e){
            return "";
        }
    }

    /**
     * 根据方法和传入的参数获取请求参数
     */
    private String getParameter(Method method, Object[] args) {
        ArrayList<Object> argList = new ArrayList<>();
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i <parameters.length; i++) {
            //将RequestBody注解修饰的参数作为请求参数
            RequestBody requestBody = parameters[i].getAnnotation(RequestBody.class);
            if(requestBody!=null){
                argList.add(args[i]);
            }
            //将RequestParam注解修饰的参数作为请求参数
            RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
            if(requestParam!=null){
                HashMap<String, Object> map = new HashMap<>(2);
                String key=parameters[i].getName();
                if (!org.apache.commons.lang3.StringUtils.isEmpty(requestParam.value())) {
                    key = requestParam.value();
                }
                map.put(key, args[i]);
                argList.add(map);
            }
        }
        if (argList.isEmpty()) {
            return "";
        }
        return argList.size()==1? JSONUtil.toJsonStr(argList.get(0)) : JSONUtil.toJsonStr(argList);
    }

}

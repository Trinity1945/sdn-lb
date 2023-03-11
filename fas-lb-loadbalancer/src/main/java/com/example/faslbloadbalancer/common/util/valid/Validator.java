package com.example.faslbloadbalancer.common.util.valid;

import com.zhangyh.common.exception.BusinessException;
import com.zhangyh.common.exception.ErrorCode;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zhangyh
 * @desc
 * @date: 2023/3/10  22:20
 */
public class Validator {
    @Resource
    LocalValidatorFactoryBean validator;

    /**
     * 参数校验
     *
     * @param params         对象参数
     * @param throwException 是否抛出异常
     * @return -
     */
    protected List<ArgumentInvalidResult> validate(Object params, Boolean throwException) {
        Errors errors = new BeanPropertyBindingResult(params, "params");
        validator.validate(params, errors);
        if (!errors.hasErrors()) {
            return null;
        }
        List<ArgumentInvalidResult> errorList = errors.getFieldErrors().stream().map(error -> {
            String field = error.getField();
            Object rejectedValue = error.getRejectedValue();
            String message = error.getDefaultMessage();
            return ArgumentInvalidResult.builder().field(field).rejectedValue(rejectedValue).reason(message).build();
        }).collect(Collectors.toList());
        if (throwException) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, errorList.toString());
        }
        return errorList;
    }
}

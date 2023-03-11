package com.zhangyh.common.exception;


import com.zhangyh.common.http.respose.BaseResponse;
import com.zhangyh.common.http.respose.ResponseHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;


/**
 * @author zhangyh
 * @Date 2023/3/6 15:40
 * @desc
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    @ExceptionHandler(value = Exception.class)
    public BaseResponse<Object> exception(Exception e) {
        log.error("unknown error: ", e);
        return ResponseHelper.failed();
    }

    @ExceptionHandler(value= BindException.class)
    public BaseResponse<Object> bindException(BindException bindException){
        BindingResult result = bindException.getBindingResult();
        if(result.hasErrors()){
            List<String> errorMessages = result.getAllErrors().stream().map(objectError -> {
                FieldError fieldError = (FieldError) objectError;
                return fieldError.getField() + "-->" + fieldError.getDefaultMessage();
            }).collect(Collectors.toList());
            return ResponseHelper.failed(ErrorCode.PARAMS_INVALID,errorMessages);
        }
        return ResponseHelper.failed(ErrorCode.PARAMS_INVALID);
    }

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("businessException: " + e.getMessage(), e);
        return ResponseHelper.failed(e.getErrorCode().getCode(), e.getMessage());
    }

}

package com.campus.common.config;

import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidationException(MethodArgumentNotValidException ex) {
        // 从异常中获取所有验证错误
        final List<FieldError> fieldErrors =ex.getBindingResult().getFieldErrors();
        // 创建一个自定义错误响应对象
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setMessage("请求参数有误");
        errorResponse.setCode(400);
        // 将每个错误添加到错误响应对象中
        for (FieldError error:fieldErrors){
            errorResponse.addError(error.getField(),error.getDefaultMessage());
        }
        return errorResponse;
    }
}
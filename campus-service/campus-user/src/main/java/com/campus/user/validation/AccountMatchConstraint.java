package com.campus.user.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD,ElementType.TYPE,ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = com.campus.user.validation.AccountMatchValidator.class)
public @interface AccountMatchConstraint {//自定义注释类，用于判断注册的账号是否已经注册
    String message()default "该账号已注册,请直接登录";//默认提示信息
    Class<?>[] groups()default {};
    Class<? extends Payload>[] payload()default {};
}



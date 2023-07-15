package com.campus.user.validation;

import com.campus.user.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TelephoneMatchValidator implements ConstraintValidator<TelephoneMatchConstraint, String> {

    @Autowired
    UserServiceImpl userService;

    @Override
    public void initialize(TelephoneMatchConstraint telephoneMatchConstraint) {//对账号进行检验

    }

    @Override
    public boolean isValid(String telephone, ConstraintValidatorContext constraintValidatorContext) {//重写校验方法
        return !userService.checkTelephoneHasRegister(telephone);//返回账号是否不在数据库中
    }


}

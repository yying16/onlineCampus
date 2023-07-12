package com.campus.user.validation;

import com.campus.user.service.UserService;
import com.campus.user.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccountMatchValidator implements ConstraintValidator<AccountMatchConstraint, String> {

    @Autowired
    UserServiceImpl userService;

    @Override
    public void initialize(AccountMatchConstraint accountMatchConstraint) {//对账号进行检验

    }

    @Override
    public boolean isValid(String account, ConstraintValidatorContext constraintValidatorContext) {//重写校验方法
        return !userService.checkAccountHasRegister(account);//返回账号是否不在数据库中
    }


}

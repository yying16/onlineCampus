package com.campus.user.service;

import com.campus.common.util.R;
import com.campus.user.domain.User;
import com.campus.user.dto.*;
import com.campus.user.vo.LoginMessage;

import java.math.BigDecimal;
import java.util.List;

public interface UserService{

    /**
     * 登录
     * @param form 登录表单
     * @return 登录消息（封装uid和token)
     * */
    LoginMessage login(LoginForm form);

    /**
     * 注册
     * */
    boolean register(RegisterForm form);

    /**
     * 生成验证码
     * */
    boolean tencentSend(String code, String phone);

    /**
     * 根据手机号和验证码登录
     * */
    LoginMessage login(LoginByCodeForm form);

    /**
     * 邮箱登录
     */
    LoginMessage login(LoginByEmailForm form);

    boolean updatePassword(UpdatePasswordForm form);

    boolean sendEmail(String emailContent, String email);

    User getUserByEmail(String email);

    void activateEmail(String email,String userId);

    /**
     * 数据校验-账号
     * */
    boolean checkAccountHasRegister(String account);

    /**
     * 数据校验-手机号
     * */
    boolean checkTelephoneHasRegister(String telephone);


    boolean checkCode(CheckCodeForm form);

    boolean checkEmailCode(CheckEmailCodeForm form);

    R updateBalance(String userId, BigDecimal balance);
}

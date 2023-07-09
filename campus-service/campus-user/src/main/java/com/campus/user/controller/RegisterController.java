package com.campus.user.controller;

import com.campus.common.util.R;
import com.campus.user.dto.RegisterForm;
import com.campus.user.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController

public class RegisterController {

    @Autowired
    UserServiceImpl userService;


    /**
     * 注册
     */
    @PostMapping("/register")
    public R register(@Valid @RequestBody RegisterForm form) {
        if(userService.register(form)){ // 注册成功
            return R.ok();
        }else{
            return R.failed();
        }
    }
}

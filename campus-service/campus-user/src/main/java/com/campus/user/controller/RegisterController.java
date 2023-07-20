package com.campus.user.controller;

import com.campus.common.util.R;
import com.campus.user.dto.RegisterForm;
import com.campus.user.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api("注册相关接口")
public class RegisterController {

    @Autowired
    UserServiceImpl userService;


    /**
     * 注册
     */
    @PostMapping("/register")
    @ApiOperation("注册")
    public R register(@Valid @RequestBody RegisterForm form) {
        if(userService.register(form)){ // 注册成功
            return R.ok();
        }else{
            return R.failed();
        }
    }
}

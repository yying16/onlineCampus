package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginForm {
    @NotNull(message = "账号/手机号/邮箱不能为空")
    @NotBlank(message = "账号/手机号/邮箱不能为空")
    String loginName; // 登录名（账号或手机号或邮箱）
    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    String password; // 密码

}

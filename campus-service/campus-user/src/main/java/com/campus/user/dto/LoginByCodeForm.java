package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class LoginByCodeForm {
    @NotNull(message = "手机号不能为空")
    @NotBlank(message = "手机号不能为空")
    String telephone; // 登录名（账号或手机号或邮箱）
    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    String code; // 验证码
}

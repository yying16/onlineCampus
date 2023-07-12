package com.campus.user.dto;

import com.campus.user.validation.AccountMatchConstraint;
import com.campus.user.validation.TelephoneMatchConstraint;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class RegisterForm {
    @NotNull(message = "账号不能为空")
    @NotBlank(message = "账号不能为空")
    @AccountMatchConstraint
    String account;
    @Pattern(regexp = "^\\S+$", message = "密码不能包含空格")
    String password;
    @NotNull(message = "用户名不能为空")
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^.{2,10}$", message = "用户名长度应为2-10个字符")
    String username;
    @NotNull(message = "电话号码不能为空")
    @NotBlank(message = "电话号码不能为空")
    @Pattern(regexp = "^1[356789]\\d{9}$", message = "手机号不合法")
    @TelephoneMatchConstraint
    String telephone;
}

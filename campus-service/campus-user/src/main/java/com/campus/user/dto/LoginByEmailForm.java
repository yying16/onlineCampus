package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/7/9 10:37
 */
@Data
public class LoginByEmailForm {

    @NotNull(message = "邮箱不能为空")
    @NotBlank(message = "邮箱不能为空")
    String email; // 邮箱
    @NotNull(message = "验证码不能为空")
    @NotBlank(message = "验证码不能为空")
    String code; // 验证码

}

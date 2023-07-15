package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/7/8 16:43
 */
@Data
public class UpdatePasswordForm {

    @NotNull(message = "密码不能为空")
    @NotBlank(message = "密码不能为空")
    private String password; // 密码

}

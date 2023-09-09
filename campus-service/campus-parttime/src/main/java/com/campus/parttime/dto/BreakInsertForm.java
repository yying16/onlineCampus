package com.campus.parttime.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * author kakakaka
 */
@Data
public class BreakInsertForm {
    String breakerId; // 违规用户Id

    @NotBlank(message = "违规内容不能为空")
    @NotNull(message = "违规内容不能为空")
    String breakText; // 违规内容

    @NotBlank(message = "用户账号不能为空")
    @NotNull(message = "用户账号不能为空")
    String breakerAccount; // 用户账号

    @NotBlank(message = "用户名不能为空")
    @NotNull(message = "用户名不能为空")
    String breakerName; // 用户名

    @NotBlank(message = "违规次数不能为空")
    @NotNull(message = "违规次数不能为空")
    Integer breakNum; // 违规次数
}

package com.campus.message.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 系统提示信息
 */
@Data
@AllArgsConstructor
public class PromptInformationForm {
    String receiver; // 接收者
    String content; // 提示内容
}

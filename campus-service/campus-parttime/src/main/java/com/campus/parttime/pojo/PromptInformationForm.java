package com.campus.parttime.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PromptInformationForm {
    String receiver; // 接收者
    String content; // 提示内容
}
package com.campus.message.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class HandleRequestForm {
    String msgId; // 请求消息id
    Boolean accept = false; // 是否接受好友申请

}

package com.campus.parttime.pojo;

import lombok.Data;

@Data
public class HandleRequestForm {
    String msgId; // 请求消息id
    Boolean accept = false; // 是否接受好友申请

}
package com.campus.message.dto;

import com.campus.message.domain.Message;
import lombok.Data;

@Data
public class MessageForm {
    String content; // 消息内容
    String sender; // 发送者id
    String receiver; // 接收者id
    Integer type; // 消息类型(0-用户消息，1-系统消息，2-请求消息)
    Boolean isPhoto; // 是否为图片格式

    public Message toMessage() {
        Message message = new Message();
        message.setContent(content);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setType(type);
        message.setIsPhoto(isPhoto);
        return message;
    }
}

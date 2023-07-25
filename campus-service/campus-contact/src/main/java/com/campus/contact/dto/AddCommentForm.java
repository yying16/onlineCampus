package com.campus.contact.dto;

import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddCommentForm {
    @ApiParam(required = true)
    String dynamicId; // 动态id
    @ApiParam(required = true)
    String senderId; // 发送者id
    @ApiParam(required = true)
    String senderName; // 发送者名称
    String receiverId; // 被回复者id（如果为空则表示该评论为直接评论）
    String receiverName; // 被回复者名称（如果为空则表示该评论为直接评论）
    @ApiParam(required = true)
    String content; // 评论内容
}

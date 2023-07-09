package com.campus.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_message")
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @TableId(type = IdType.ASSIGN_ID)
    String messageId;       // 消息id
    Integer type;           //消息类型(0-用户消息，1-系统消息，2-请求消息，3-自动回复消息)
    String content;         // 消息内容
    Boolean isPhoto;         //是否为图片格式
    String sender;          // 发送者
    String receiver;        // 接收者
    Integer status;         //消息状态(0-未读，1-已读,2-异常)
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;      // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;       // 更新时间

    public Message copy(){
        Message ret = new Message();
        ret.setMessageId(this.getMessageId());
        ret.setType(this.getType());
        ret.setContent(this.getContent());
        ret.setIsPhoto(this.getIsPhoto());
        ret.setSender(this.getSender());
        ret.setReceiver(this.getReceiver());
        ret.setStatus(this.getStatus());
        ret.setDeleted(this.getDeleted());
        ret.setCreateTime(this.getCreateTime());
        ret.setUpdateTime(this.getUpdateTime());
        return ret;
    }
}

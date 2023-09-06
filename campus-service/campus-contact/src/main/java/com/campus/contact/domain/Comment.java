package com.campus.contact.domain;

import com.campus.common.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * author yying
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment implements Comparable<Comment>{
    private String uuid;
    private String _class;
    private String content; // 评论内容
    private String sender; // 发送者id
    private String senderImage; // 发送者头像
    private String receiver; // 被回复者id（如果为空则表示该评论为直接评论）
    private String senderName; // 发布者名称（冗余数据）
    private String receiverName; // 被回复者名称（冗余数据）（如果为空则表示该评论为直接评论）
    private Boolean deleted = false; // 删除标志
    private String createTime; // 创建时间
    private String updateTime; // 更新时间

    @Override
    public int compareTo(@NotNull Comment o) {
        long a = TimeUtil.getTimeStamp(this.createTime);
        long b = TimeUtil.getTimeStamp(o.createTime);
        return Long.compare(b,a);
    }
}
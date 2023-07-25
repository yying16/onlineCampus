package com.campus.message.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InitUserMessageData implements Comparable<InitUserMessageData>{
    String userId; // 用户id
    String username; // 用户名
    String userImage; // 用户头像
    String recentContent; // 最近一次的聊天内容(!!如果没有聊天记录，则为空)
    String recentTime; // 最近一次的聊天时间(!!如果没有聊天记录，则为空)

    @Override
    public int compareTo(@NotNull InitUserMessageData o) { // 按照最近一次聊天时间降序排序
        return o.getRecentTime().compareTo(this.getRecentTime());
    }
}

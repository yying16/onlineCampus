package com.campus.message.pojo;

import lombok.Data;

@Data
public class MatchUser {
    String userId; // 用户id
    String account; // 用户账号
    String userImage; // 用户头像
    String username; // 用户名
    String telephone; // 电话号码
    String email; // 邮箱
    String credit; // 信用
}

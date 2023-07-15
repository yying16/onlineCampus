package com.campus.user.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * @auther xiaolin
 * @create 2023/7/13 14:39
 */
@Data
public class UpdateUserForm {
    String userId; // 用户id
    String account;     //账号
    String username;    //用户名(长度为2-12)
    String telephone;   //手机号码
    String email;       //邮箱
    String autoReply;   //自动回复内容
    String address;     //地址
}

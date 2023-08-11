package com.campus.user.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Pattern;

import java.math.BigDecimal;

@Data
@TableName("t_user")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    String userId; // 用户id
    String account;     //账号
    String originPassword;    //原始密码
    String password;    //密码
    String username;    //用户名(长度为2-12)
    String telephone;   //手机号码
    String userImage;   //用户头像
    String email;       //邮箱
    String autoReply;   //自动回复内容
    Boolean status;     //身份（true为管理员，false为普通用户）
    Integer credit;     //信用
    Integer breakNum;   //违规次数

    String address;     //地址

    String consignee;   //收货人

    BigDecimal balance; //余额

    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String createTime; // 创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String updateTime; // 更新时间
}

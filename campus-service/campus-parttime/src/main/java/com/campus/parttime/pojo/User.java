package com.campus.parttime.pojo;


import com.baomidou.mybatisplus.annotation.*;
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
    Integer credit;     //信用值
    Integer breakNum;   //违规次数

    String address;     //地址

    String consignee;   //收货人

    BigDecimal balance; //余额

    Integer gender;     //性别

    Integer grade;      //年级

    Integer campus;   //校区


    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String createTime; // 创建时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String updateTime; // 更新时间

    Integer auth; //是否认证

    @TableField(value = "auth_front_image")
    String authFrontImage; //学生证正面照片

    @TableField(value = "auth_back_image")
    String authBackImage; // 学生证反面照片
}

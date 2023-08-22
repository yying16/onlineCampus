package com.campus.parttime.vo;

import lombok.Data;

/**
 * author kakakaka
 */

@Data

public class UserInfo {
    String userImage;   //用户头像
    String account;     //账号
    String username;    //用户名(长度为2-12)
    Integer gender;     //性别
    Integer credit;     //信用值
    Integer breakNum;   //违规次数
    String telephone;   //手机号码
    String email;       //邮箱
    Integer grade;      //年级
    Integer campus;   //校区
}

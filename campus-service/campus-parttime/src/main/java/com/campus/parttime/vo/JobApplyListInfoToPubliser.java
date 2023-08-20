package com.campus.parttime.vo;

import lombok.Data;

/**
 * author kakakaka
 */
@Data
public class JobApplyListInfoToPubliser {
    String username; //申请用户名
    String applicantId;  // 申请用户Id
    Integer gender;     //性别
    Integer credit;     //信用值
    Integer status; // 兼职申请状态（0-已申请，1-已通过，2-已拒绝）
    String createTime;  //创建时间
}

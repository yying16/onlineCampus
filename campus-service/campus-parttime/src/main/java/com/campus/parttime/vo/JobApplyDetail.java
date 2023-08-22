package com.campus.parttime.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author kakakaka
 */
@Data

public class JobApplyDetail {
    String jobTitle;    // 兼职职位名称
    BigDecimal salary;      // 兼职薪资
    Integer status; // 兼职申请状态（0-已申请，1-已通过，2-已拒绝）
    String jobContent;  // 兼职详情
    List<String> images;  // 图片
    Integer location; // 兼职所在校区(0-佛山校区，1-广州校区)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    Integer workingDays; // 工作天数
    String deadline;    // 截止时间
    Integer recruitNum;// 需招聘人数

    String applicantUserName; // 申请人用户名
    String applicantTelephone; // 申请人电话
    String publisherUserName; // 发布者用户名
    String publisherTelephone; // 发布者电话

    String applicationId; // 兼职申请表编号
    String jobId;   // 兼职职位编号

    String createTime;  // 申请表创建时间
    String updateTime;  // 申请表更新时间
}

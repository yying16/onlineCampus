package com.campus.parttime.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author kakakaka
 */

@Data
public class JobOperationDetail {
    String jobTitle; // 兼职标题
    BigDecimal salary; // 兼职薪资
    Integer status; // 兼职执行状态（0-进行中，1-已完成，2-已超时，3-已取消，4-确认完成）
    String jobContent; // 兼职详情
    List<String> images; // 兼职图片
    Integer location; // 兼职所在校区(0-佛山校区，1-广州校区)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    Integer workingDays; // 工作天数
    String deadline; // 截止时间

    String operationId;   //执行编号
    String jobId; // 关联的兼职职位编号

    String applicantUserName; // 执行者用户名
    String applicantTelephone; // 执行者电话
    String publisherUserName; // 发布者用户名
    String publisherTelephone; // 发布者电话

    String createTime; // 执行表创建时间
    String updateTime; // 执行表更新时间


    String feedback_from_publisher_to_applicant;    // 发布者给申请者的反馈
    String feedback_from_applicant_to_publisher;    // 申请者给发布者的反馈
}

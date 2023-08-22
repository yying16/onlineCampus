package com.campus.parttime.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author kakakaka
 */
@Data
public class MyPublishedList {
    String jobId; // 兼职Id
    String jobTitle; // 兼职标题
    BigDecimal salary;  // 兼职薪资
    Integer status; // 兼职职位状态(0-开启，1-关闭，2-招满，3-完成)
    Integer location; // 兼职所在校区(0-佛山校区，1-广州校区)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    Integer recruitNum; // 需招聘人数
    String updateTime;  //更新时间
}

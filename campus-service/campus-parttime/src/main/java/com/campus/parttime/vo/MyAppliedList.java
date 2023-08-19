package com.campus.parttime.vo;

import lombok.Data;
import java.math.BigDecimal;

/**
 * author kakakaka
 */
@Data
public class MyAppliedList {
    String jobTitle; // 兼职标题
    BigDecimal salary;  // 兼职薪资
    Integer applyStatus; // 兼职申请状态（0-已申请，1-已通过，2-已拒绝）
    Integer location; // 兼职所在校区(0-佛山校区，1-广州校区)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    String updateTime;  //更新时间
}

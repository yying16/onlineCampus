package com.campus.parttime.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * author kakakaka
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

public class JobInsertForm {
    String publisherId; // 发布者编号
    String jobTitle;    // 兼职职位名称
    String jobContent;  // 兼职详情
    String salary;      // 兼职薪资
    String deadline;    // 截止时间
    Integer location;   //兼职所在校区(0-佛山校区，1-广州校区)
    List<String> photo;  // 图片
    Integer recruitNum; // 需招聘人数
    Integer term;       // 兼职任期(0-短期，1-长期)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    Integer workingDays;    // 工作天数
}

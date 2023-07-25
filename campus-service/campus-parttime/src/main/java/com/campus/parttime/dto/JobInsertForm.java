package com.campus.parttime.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    Integer location; //兼职所在校区(0-佛山校区，1-广州校区)
    Integer recruitNum;// 需招聘人数
}

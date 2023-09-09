package com.campus.parttime.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * author kakakaka
 */

@Data
@AllArgsConstructor
@NoArgsConstructor

public class JobInsertForm {
    @NotBlank(message = "发布者编号不能为空")
    @NotNull(message = "发布者编号不能为空")
    String publisherId; // 发布者编号

    @NotBlank(message = "兼职职位名称不能为空")
    @NotNull(message = "兼职职位名称不能为空")
    String jobTitle;    // 兼职职位名称

    @NotBlank(message = "兼职详情不能为空")
    @NotNull(message = "兼职详情不能为空")
    String jobContent;  // 兼职详情

    @NotBlank(message = "兼职薪资不能为空")
    @NotNull(message = "兼职薪资不能为空")
    BigDecimal salary;      // 兼职薪资

    @NotBlank(message = "截止时间不能为空")
    @NotNull(message = "截止时间不能为空")
    String deadline;    // 截止时间

    @NotBlank(message = "兼职所在校区不能为空")
    @NotNull(message = "兼职所在校区不能为空")
    Integer location;   //兼职所在校区(0-佛山校区，1-广州校区)

    List<String> images;  // 图片

    @NotBlank(message = "需招聘人数不能为空")
    @NotNull(message = "需招聘人数不能为空")
    Integer recruitNum; // 需招聘人数

    @NotBlank(message = "兼职任期不能为空")
    @NotNull(message = "兼职任期不能为空")
    Integer term;       // 兼职任期(0-短期，1-长期)

    @NotBlank(message = "兼职分类不能为空")
    @NotNull(message = "兼职分类不能为空")
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)

    Integer workingDays;    // 工作天数
}

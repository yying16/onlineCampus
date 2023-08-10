package com.campus.parttime.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@TableName("t_job")
@AllArgsConstructor
@NoArgsConstructor
public class Job {
    @TableId(type = IdType.ASSIGN_ID)
    String jobId;       // 兼职编号
    String publisherId; // 发布者编号
    String jobTitle;    // 兼职职位名称
    String jobContent;  // 兼职详情
    BigDecimal salary;      // 兼职薪资
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String deadline;    // 截止时间
    Integer location; // 兼职所在校区(0-佛山校区，1-广州校区)
    Integer term; // 兼职任期(0-短期，1-长期)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    Integer workingDays; // 工作天数
    @TableField(exist = false)
    List<String> images;  // 图片
    Integer status;     // 兼职职位状态(0-开启，1-关闭，2-招满，3-完成)
    Integer recruitNum;// 需招聘人数
    Integer passedNum; // 已通过人数
    Integer applyNum;  // 已申请人数
    Integer finishNum; // 已完成人数
    Integer visitNum; //访问次数
    Integer likeNum; // 点赞人数
    Integer favoritesNum; // 收藏人数
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    // 逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  // 更新时间

    public Job copy(){
        Job job = new Job();
        return job;
    }
}


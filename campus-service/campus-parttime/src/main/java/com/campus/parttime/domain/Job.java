package com.campus.parttime.domain;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    String salary;      // 兼职薪资
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String deadline;    // 截止时间
    Integer location; //兼职所在校区(0-佛山校区，1-广州校区)
    Integer status;     // 兼职职位状态(0-招满，1-关闭，2-开启)
    Integer recruitNum;// 需招聘人数
    Integer passedNum; // 已通过人数
    Integer applyNum;  // 已申请人数
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


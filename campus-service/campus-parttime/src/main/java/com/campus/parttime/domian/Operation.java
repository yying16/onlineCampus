package com.campus.parttime.domian;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_operation")
public class Operation {
    @TableId(type = IdType.ASSIGN_ID)
    String operationId;   //执行编号
    String jobId;         //关联的兼职职位编号
    String applicantId;   //兼职申请者编号
    String publisherId;   //雇主编号
    Integer status;       //执行状态(0-进行中，1-已完成，2-已取消)
    String feedback_from_publisher_to_applicant;    // 发布者给申请者的反馈
    String feedback_from_applicant_to_publisher;    // 申请者给发布者的反馈
    @TableLogic(value = "false",delval = "true")
    Boolean deleted;    //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  //更新时间
}

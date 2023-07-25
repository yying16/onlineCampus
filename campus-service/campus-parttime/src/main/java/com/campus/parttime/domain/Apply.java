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
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_apply")
public class Apply {
    @TableId(type = IdType.ASSIGN_ID)
    String applicationId; // 兼职申请表编号
    String jobId;   // 兼职职位编号
    String applicantId;  // 申请用户编号
    Integer status; // 兼职申请状态（0-已申请，1-已通过，2-已完成，3-已拒绝）
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  //更新时间
}

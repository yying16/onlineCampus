package com.campus.parttime.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * author kakakaka
 */
@Data

public class JobApplyDetail {
    String applicationId; // 兼职申请表编号
    String jobId;   // 兼职职位编号
    String applicantId;  // 申请用户编号
    Integer status; // 兼职申请状态（0-已申请，1-已通过，2-已拒绝）
    String createTime;  //创建时间
    String updateTime;  //更新时间
}

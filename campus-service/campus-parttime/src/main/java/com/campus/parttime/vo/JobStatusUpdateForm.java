package com.campus.parttime.vo;

import lombok.Data;
import com.campus.parttime.domain.Job;
/**
 * author kakakaka
 */

@Data

public class JobStatusUpdateForm {
    String jobId;       // 兼职编号
    Integer recruitNum;// 需招聘人数
    Integer passedNum; // 已通过人数
    Integer status;     // 兼职职位状态(0-招满，1-关闭，2-开启)
    Boolean deleted;    // 逻辑删除
}

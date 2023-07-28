package com.campus.parttime.dto;

import lombok.Data;

/**
 * author kakakaka
 */

@Data

public class ApplyStatusUpdateForm {
    String applicationId; // 兼职申请表编号
    Integer status; // 兼职申请状态（0-已申请，1-已通过，2-已完成，3-已拒绝）
}


package com.campus.parttime.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * author kakakaka
 */

@Data

public class ApplyStatusUpdateForm {
    String applicationId; // 兼职申请表编号

    @NotBlank(message = "兼职申请状态不能为空")
    @NotNull(message = "兼职申请状态不能为空")
    Integer status; // 兼职申请状态（0-已申请，1-已通过，2-已拒绝）
}


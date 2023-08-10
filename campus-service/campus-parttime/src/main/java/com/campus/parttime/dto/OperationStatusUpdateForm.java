package com.campus.parttime.dto;

import lombok.Data;

/**
 * author kakakaka
 */
@Data
public class OperationStatusUpdateForm {
    String operationId;   //执行编号
    String jobId;         //关联的兼职职位编号
    Integer status;       //执行状态(0-进行中，1-已完成，2-已取消, 3-确定完成)
}

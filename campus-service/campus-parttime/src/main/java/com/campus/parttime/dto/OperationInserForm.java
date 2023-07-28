package com.campus.parttime.dto;

import lombok.Data;

/**
 * author kakakaka
 */

@Data

public class OperationInserForm {
    String jobId;         //关联的兼职职位编号
    String applicantId;   //兼职申请者编号
    String publisherId;   //雇主编号
}

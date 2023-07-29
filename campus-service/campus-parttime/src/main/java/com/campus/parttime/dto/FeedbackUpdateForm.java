package com.campus.parttime.dto;

import lombok.Data;

/**
 * author kakakaka
 */
@Data
public class FeedbackUpdateForm {
    String operationId;   //执行编号
    String feedback_from_publisher_to_applicant;    // 发布者给申请者的反馈
    String feedback_from_applicant_to_publisher;    // 申请者给发布者的反馈
}

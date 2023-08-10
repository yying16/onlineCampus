package com.campus.user.dto;

/**
 * author kakakaka
 */

public class ReportInsertForm {
    String reporter_id; //举报人的Id
    String reporter_name; // 举报人的姓名
    String reported_id; // 被举报人id
    String reported_name; // 被举报人姓名
    String report_content; // 举报详情
    Integer report_type; // 举报类型(0-其他，1-不当言论，2-侵犯版权，3-违规行为，4-内容低俗，5-不当广告，6-虚假信息）
    String other_id; // 关联id
    String other_type; // 关联表名
}

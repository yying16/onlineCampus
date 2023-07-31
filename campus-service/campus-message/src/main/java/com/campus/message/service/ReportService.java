package com.campus.message.service;

import com.campus.message.domain.Report;

public interface ReportService {

    /**
     * 提交举报
     * 转发系统消息到举报人
     * */
    boolean submitReport(Report report);

    /**
     * 确认举报情况
     *
     * 将举报元组状态修改为确认
     * 扣除对方一定的信用值
     * 转发系统消息到被举报人
     * */
    boolean confirmReport(String reportId,Integer creditChange);

    /**
     * 驳回举报
     *
     * 将举报元组状态修改为驳回
     * 转发系统消息到举报人
     * */
    boolean rejectReport(String reportId);

}

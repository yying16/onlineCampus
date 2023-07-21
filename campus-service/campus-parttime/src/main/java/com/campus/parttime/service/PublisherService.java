package com.campus.parttime.service;

import com.campus.parttime.domian.Job;

public interface PublisherService {
    /**
     * 1.新增兼职信息
     */
    boolean insertJob(Job job);

    /**
     * 2.编辑兼职信息
     */
    boolean updateById(String jobId);

    /**
     * 3.删除兼职信息
     */
    boolean deleteJob(String jobId);

    /**
     * 4.修改订单执行属性：用于订单完成确定
     */
    boolean updateStatusById(String operationId);

    /**
     * 5.修改兼职申请状态
     */
    boolean updateApplyStatus(String applicationId, String applicantId, String publisherId);
}

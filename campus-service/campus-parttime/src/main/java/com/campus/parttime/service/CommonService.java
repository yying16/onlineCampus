package com.campus.parttime.service;

import com.alibaba.fastjson.JSONObject;
import com.campus.parttime.domian.Job;

import java.util.List;

public interface CommonService {
    /**
     * 1.更新兼职信息（系统操作）
     */
    boolean setJobStatus(String jobId);

    /**
     * 2.根据关键信息查找兼职列表
     */
    List searchByKey(String title, Integer location, Integer status);

    /**
     * 3.根据兼职编号（jobId）查找兼职信息
     */
    JSONObject searchById(String jobId);

    /**
     * 4.新增执行订单记录（申请成功后）
     */
    boolean addJobOperation(String jobId, String applicantId, String publisherId);

    /**
     * 5.删除兼职执行订单
     */
    boolean deleteJobOperation(String operationId);

    /**
     * 6.兼职订单反馈提交
     */
    boolean updateFeedbackById(String operationId,String userId);

    /**
     * 7.查看兼职职位详情
     */
     Job getJobDetails(String jobId);

    /**
     * 8.初始化兼职信息栏：获取兼职列表
     */
    List getJobList();
}

package com.campus.parttime.service;

public interface PublisherService {
    /**
     * 1.新增兼职信息:boolean insertJob(Job job); // boolean insertMySql(T t);
     */

    /**
     * 2.编辑兼职信息:boolean updateById(String jobId); // boolean insertMySql(T t);
     */

    /**
     * 3.删除兼职信息:boolean deleteJob(String jobId); // boolean delete(String jobId, Class clazz);
     */

    /**
     * 4.修改订单执行属性：用于订单完成确定:boolean updateStatusById(String operationId); // boolean updateMySql(T t);
     */

    /**
     * 5.修改兼职申请状态:boolean updateApplyStatus(String applicationId, String applicantId, String publisherId);// boolean updateMySql(T t);
     */
}

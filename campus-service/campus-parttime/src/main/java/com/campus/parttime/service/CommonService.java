package com.campus.parttime.service;

public interface CommonService {
    /**
     * 1.更新兼职状态（系统操作）:boolean setJobStatus(String jobId); //boolean update(T t)
     */

    /**
     * 2.根据关键信息查找兼职列表: List searchByKey(String title, Integer location, Integer status); //object search(Map<String,Object>condition, Class clazz);
     */

    /**
     * 3.根据兼职编号（jobId）查找兼职信息:JSONObject searchById(String jobId); // object search(String id, Class clazz);
     */

    /**
     * 4.新增执行订单记录（申请成功后）:boolean addJobOperation(String jobId, String applicantId, String publisherId); // String insert(T t);
     */

    /**
     * 5.删除兼职执行订单:boolean deleteJobOperation(String operationId); // boolean deleteById(String id, Class clazz);
     */

    /**
     * 6.兼职订单反馈提交:boolean updateFeedbackById(String operationId,String userId); // boolean updateMySql(T t);
     */

    /**
     * 7.查看兼职职位详情:Job getJobDetails(String jobId); // Object search(String id, Class clazz);
     */

    /**
     * 8.初始化兼职信息栏：获取兼职列表（懒加载）：List getJobList(); // List loadData(int num, Class clazz);
     */

}

package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Apply;
import com.campus.parttime.domain.Job;
import com.campus.parttime.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface JobDao extends BaseMapper<Job> {
    @Select("select count(*) from  t_job where publisher_id=#{publisherId} AND deleted=0")
    Integer searchPersonalPostJobNum(String publisherId);

    @Select("select CONCAT(month(create_time),'-',day(create_time)),COUNT(*) from t_job where create_time between DATE_SUB(now(), interval 30 day) and now() group by CONCAT(month(create_time),'-',day(create_time))")
    Map<String,Integer> searchPostJobInMonth();

    @Select("select * from t_apply where job_id=#{job_Id} and deleted=0")
    List<Apply> SearchApplyListByJobId(String jobId);

    @Select("select * from t_job where publisher_id=#{publisherId} and deleted=0")
    List<Job> searchJobList(String publisherId);

    @Select("select * from t_user where user_id=#{userId} and deleted=0")
    User searchUserInfo(String userId);

    @Select("select job_id from t_job where deleted=0")
    List<String> list();
}

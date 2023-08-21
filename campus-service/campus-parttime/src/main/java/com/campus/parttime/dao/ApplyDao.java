package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Apply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;


@Mapper
public interface ApplyDao extends BaseMapper<Apply> {
    @Update("update t_apply set deleted=1 where job_id = #{jobId}")
    void deleteApplyByJobId(String jobId);

    @Select("select applicant_id from t_apply where job_id = #{jobId}")
    List<String> selectByJobId(String jobId);

    @Update("update t_user set credit=t_user.credit+5 where user_id= #{userId} AND deleted=0" )
    void addCreditByJobId(String userId);

    @Select("select credit from t_user where user_id= #{userId}")
    Integer selectCreditByJobId(String userId);



    @Select("select count(*) from t_apply where applicant_id=#{applicantId} AND deleted=0")
    Integer searchPersonalApplyJobNum(String applicantId);

    @Select("select * from t_apply where applicant_id=#{applicantId} and deleted=0")
    List<Apply> searchApplyListByApplicantId(String applicantId);

    @Select("select * from t_apply where job_id=#{jobId} and applicant_id=#{applicantId} and deleted=0")
    Apply isJobApplyExist(String jobId, String applicantId);

    @Select("select application_id from t_apply where applicant_id=#{applicantId} and job_id=#{jobId} and deleted=0")
    String searchApplyIsExist(String applicantId, String jobId);


}

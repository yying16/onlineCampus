package com.campus.parttime.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Record;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface RecordDao extends BaseMapper<Record> {
    @Select("select job_record_id from t_job_record where job_id = #{jobId} and user_id=#{userId}")
    String searchRecordIsExist(String jobId, String userId);


    @Update("update t_job_record set score = score+1 where job_id = #{jobId} and user_id = #{userId}")
    void updateRecoreScore(String jobId, String userId);
}

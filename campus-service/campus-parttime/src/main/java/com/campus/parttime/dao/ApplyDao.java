package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Apply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface ApplyDao extends BaseMapper<Apply> {
    @Update("update t_apply set deleted=1 where job_id = #{jobId}")
    void deleteApplyByJobId(String jobId);

}

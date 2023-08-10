package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Job;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.Map;

@Mapper
public interface JobDao extends BaseMapper<Job> {
    @Select("select count(*) from  t_job where publisher_id=#{publisherId} AND deleted=0")
    Integer searchPersonalPostJobNum(String publisherId);

    @Select("select CONCAT(month(create_time),'-',day(create_time)),COUNT(*) from t_job where create_time between DATE_SUB(now(), interval 30 day) and now() group by CONCAT(month(create_time),'-',day(create_time))")
    Map<String,Integer> searchPostJobInMonth();




}

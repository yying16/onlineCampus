package com.campus.parttime.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domian.Job;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface JobDao extends BaseMapper<Job> {
}

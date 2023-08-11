package com.campus.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.user.domain.Report;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReportDao extends BaseMapper<Report> {
}

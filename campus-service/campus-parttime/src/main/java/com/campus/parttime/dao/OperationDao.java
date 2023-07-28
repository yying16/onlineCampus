package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Operation;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationDao extends BaseMapper<Operation> {
}

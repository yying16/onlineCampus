package com.campus.test.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.test.domain.Test;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestDao extends BaseMapper<Test> {

}

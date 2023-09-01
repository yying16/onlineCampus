package com.campus.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.user.domain.Search;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SearchDao extends BaseMapper<Search> {
}

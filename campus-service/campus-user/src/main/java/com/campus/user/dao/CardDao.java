package com.campus.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.user.domain.Card;
import org.apache.ibatis.annotations.Mapper;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:47
 */
@Mapper
public interface CardDao extends BaseMapper<Card> {
}

package com.campus.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.message.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MessageDao extends BaseMapper<Message> {

    @Select("select * from t_user where user_id = #{userId}")
    Map getUserDetail(String userId);

    @Select("select * from t_message where deleted = 0 order by create_time desc limit #{num},20")
    List<Message> lazyLoading(Integer num);
}

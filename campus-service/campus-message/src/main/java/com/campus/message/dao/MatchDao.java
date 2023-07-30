package com.campus.message.dao;

import com.alibaba.fastjson.JSONObject;
import com.campus.message.pojo.MatchUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MatchDao {

    @Select("select * from t_user where deleted = 0 and user_id = #{uid}")
    MatchUser getUser(String uid);
}

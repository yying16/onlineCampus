package com.campus.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.message.domain.Message;
import com.campus.message.pojo.LazyLoadPojo;
import com.campus.message.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MessageDao extends BaseMapper<Message> {

    @Select("select * from t_user where user_id = #{userId}")
    User getUserDetail(String userId);

    @Select("select * from t_message where deleted = 0 and type = 0 and (((sender = #{uid} and receiver = #{fid})) or ((sender = #{fid} and receiver = #{uid}))) order by create_time desc limit #{num},20")
    List<Message> lazyLoading(LazyLoadPojo pojo);

    //获取系统消息
    @Select("select * from t_message where type = 1 and receiver = #{uid} and deleted = 0 order by create_time desc")
    List<Message> getSystemMessage(String uid);

    //获取请求消息
    @Select("select * from t_message where type = 2 and receiver = #{uid} and deleted = 0 order by create_time desc")
    List<Message> getRequestMessage(String uid);

    @Select("select * from t_message where deleted = 0 and type = 0 and ((sender = #{uid}) or (receiver = #{uid})) order by create_time desc limit 1200")
    List<Message> getMyAllDialog(String uid);

    @Select("select auto_reply from t_user where user_id = #{uid}")
    String getAutoReply(String uid);


    //获取所有普通用户id
    @Select("select user_id from t_user where deleted = 0 and status = 0")
    List<String> getAllUserId();

    //获取所有普通用户id
    @Select("update t_message t set t.status = 1 where (sender = #{uid} or receiver = #{uid}) and t.status = 0 and deleted = 0")
    void clearUnRead(String uid);

}

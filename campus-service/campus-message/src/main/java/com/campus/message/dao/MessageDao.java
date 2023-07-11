package com.campus.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.message.domain.Message;
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

    @Select("select * from t_message where deleted = 0 order by create_time desc limit #{num},20")
    List<Message> lazyLoading(Integer num);

    // 获取myId对应的好友列表
    @Select("select user_id, account,username,telephone,email,credit from t_relationship tr,t_user tu where ((tr.sender = #{myId} and tr.receiver = tu.user_id) or (tr.receiver = #{myId} and tr.sender = tu.user_id) ) and tr.status = 0 and tr.deleted = 0")
    List<User> getFriends(String myId);

    //获取系统消息
    @Select("select * from t_message where type = 1 and receiver = #{uid} and deleted = 0 order by create_time desc")
    List<Message> getSystemMessage(String uid);

    //获取请求消息
    @Select("select * from t_message where type = 2 and receiver = #{uid} and deleted = 0 order by create_time desc")
    List<Message> getRequestMessage(String uid);

    @Select("select * from t_message where sender = #{uid} and deleted = 0 and type = 0 UNION select * from t_message where sender = #{uid} and deleted = 0 and type = 0 order by create_time desc limit 300")
    List<Message> getMyAllDialog(String uid);

    @Select("select auto_reply from t_user where user_id = #{uid}")
    String getAutoReply(String uid);


    //获取所有普通用户id
    @Select("select user_id from t_user where deleted = 0 and status = 0")
    List<String> getAllUserId();

}

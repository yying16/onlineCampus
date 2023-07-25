package com.campus.message.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.message.domain.Relationship;
import com.campus.message.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RelationshipDao extends BaseMapper<Relationship> {

    // 获取myId对应的好友列表
    @Select("select user_id, account,username,user_image,telephone,email,credit from t_relationship tr,t_user tu where ((tr.sender = #{myId} and tr.receiver = tu.user_id) or (tr.receiver = #{myId} and tr.sender = tu.user_id) ) and tr.status = 0 and tr.deleted = 0")
    List<User> getFriends(String myId);


    @Update("update t_relationship set deleted = 1 where (sender = #{uid} and receiver = #{friendId}) or (sender = #{friendId} and receiver = #{uid})")
    void deleteFriend(@Param("uid")String uid,@Param("friendId")String friendId);

    @Update("update t_relationship set status = 1 where (sender = #{uid} and receiver = #{friendId}) or (sender = #{friendId} and receiver = #{uid})")
    void blockFriend(@Param("uid")String uid,@Param("friendId")String friendId);
}

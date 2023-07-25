package com.campus.message.service;

import com.campus.message.domain.Message;
import com.campus.message.domain.Relationship;
import com.campus.message.pojo.User;

import java.util.List;

public interface RelationshipService {


    /**
     * 添加好友
     * @param relationship 新增关系
     * */
    boolean addFriend(Relationship relationship);


    /**
     * 查看我的好友列表
     * */
    List<User> getFriends(String uid);


    /**
     * 删除好友
     * 删除数据库
     * 删除缓存
     * @param uid 当前登录用户
     * @param friendId 好友id
     * */
    boolean deleteFriend(String uid,String friendId);

    /**
     * 屏蔽好友
     *
     * 更新数据库
     * 删除缓存
     * @param uid 当前登录用户
     * @param friendId 好友id
     * */
    boolean blockFriend(String uid,String friendId);
}

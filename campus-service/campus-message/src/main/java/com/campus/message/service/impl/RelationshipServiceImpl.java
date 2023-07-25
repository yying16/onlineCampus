package com.campus.message.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.campus.message.dao.RelationshipDao;
import com.campus.message.domain.Relationship;
import com.campus.message.pojo.User;
import com.campus.message.service.RelationshipService;
import com.campus.message.socket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RelationshipDao relationshipDao;

    /**
     * 添加好友
     *
     * 调用该方法的人一定是当前登录用户接受对方的好友请求，所以uid==relationship.receiver
     * @param relationship 新增关系
     * */
    @Override
    public boolean addFriend(Relationship relationship) {
        relationshipDao.insert(relationship); // 写入数据库
        String h1 = "relationship" + relationship.getReceiver();
        redisTemplate.delete(h1); // 删除好友缓存(下次查询时会自动更新)
        String h2 = "relationship" + relationship.getSender();
        redisTemplate.delete(h2); // 删除好友缓存(下次查询时会自动更新)
        return true;
    }

    /**
     * 查看我的好友列表
     */
    @Override
    public List<User> getFriends(String uid) {
        String h = "relationship" + uid;
        String friendArrayStr = redisTemplate.opsForValue().get(h);
        if (friendArrayStr == null) { // 没有设置缓存
            List<User> friends = relationshipDao.getFriends(uid); // 获取好友列表
            if (friends != null) {
                redisTemplate.opsForValue().set(h, JSONArray.toJSONString(friends));//设置缓存
                return friends;
            } else {
                return null;
            }
        } else {
            List<User> ret = JSONArray.parseArray(friendArrayStr, User.class);
            return ret;
        }
    }

    /**
     * 删除好友
     * 删除数据库
     * 删除缓存
     *
     * @param uid      当前登录用户
     * @param friendId 好友id
     */
    @Override
    public boolean deleteFriend(String uid, String friendId) {
        try {
            relationshipDao.deleteFriend(uid, friendId);
            String h = "relationship" + uid;
            redisTemplate.delete(h); // 删除好友缓存
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean blockFriend(String uid, String friendId) {
        try {
            relationshipDao.blockFriend(uid, friendId);
            String h = "relationship" + uid;
            redisTemplate.delete(h); // 删除好友缓存
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}

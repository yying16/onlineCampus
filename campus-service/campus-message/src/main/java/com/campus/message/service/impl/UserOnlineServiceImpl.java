package com.campus.message.service.impl;

import com.campus.message.service.UserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
public class UserOnlineServiceImpl implements UserOnlineService {

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String onlineUserKey = "ONLINE_USERS";

    /**
     * 判断用户是否在线
     *
     * @param uid
     */
    @Override
    public boolean isOnline(String uid) {
        return redisTemplate.opsForHash().hasKey(onlineUserKey,uid);
    }
}

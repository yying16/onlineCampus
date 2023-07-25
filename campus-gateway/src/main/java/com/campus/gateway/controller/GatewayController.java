package com.campus.gateway.controller;

import com.alibaba.fastjson.JSONObject;
import com.campus.gateway.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
public class GatewayController {

    final static int tokenCacheTime = 5; //token缓存时间 5小时（单位小时）

    @Autowired
    StringRedisTemplate redisTemplate;

    @PostMapping("/generalToken")
    public String generalToken(@RequestBody JSONObject user) {
        String uid = String.valueOf(user.get("userId"));
        String token = TokenUtil.generateToken(uid, user, tokenCacheTime);
        redisTemplate.opsForValue().set("user"+uid, user.toJSONString(), tokenCacheTime, TimeUnit.HOURS); //设置token5小时有效期缓存
        return token;
    }
}

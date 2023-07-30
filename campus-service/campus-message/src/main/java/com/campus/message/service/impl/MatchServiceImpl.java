package com.campus.message.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.campus.message.dao.MatchDao;
import com.campus.message.pojo.MatchUser;
import com.campus.message.service.MatchService;
import com.campus.message.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchServiceImpl implements MatchService {

    private final static String MATCH_TOPIC = "match";
    private final static String MATCH = "MATCH";
    private final static String CACHEMATCH = "CACHEMATCH";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MatchDao matchDao;

    @Autowired
    MessageServiceImpl messageService;

    /**
     * 加入匹配列表
     * <p>
     * 发送消息到中间件
     *
     * @param uid
     */
    @Override
    public boolean match(String uid) {
        MatchUser user = matchDao.getUser(uid);
        if (user != null) { // 能查找到对应的详细数据
            String userStr = JSONObject.toJSONString(user);
            kafkaTemplate.send(MATCH_TOPIC, MATCH,userStr);
        }
        return true;
    }

    /**
     * 取消匹配
     * <p>
     * 发送取消消息到中间件
     *
     * @param uid
     */
    @Override
    public boolean cacheMatch(String uid) {
        kafkaTemplate.send(MATCH_TOPIC,CACHEMATCH,uid);
        return true;
    }


    @KafkaListener(topics = "match")
    public void tradeListener(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String key = record.key();
        if(key.equals(MATCH)){
            String userStr = record.value();
            MatchUser user = JSONObject.parseObject(userStr, MatchUser.class);
            List<Object> match = redisTemplate.opsForHash().values("match"); // 获取当前正在匹配的列表
            //模拟匹配
            if (match.size() > 0) { // 如果有人正在匹配
                MatchUser matchUser = JSONObject.parseObject(String.valueOf(match.get(0)), MatchUser.class);
                redisTemplate.opsForHash().delete("match",matchUser.getUserId());
                //websocket 通知前端
                messageService.sendPromptInformation(matchUser.getUserId(), "匹配成功，快去聊聊吧");
            }else{
                redisTemplate.opsForHash().put("match", user.getUserId(), userStr);
            }
        }else if(key.equals(CACHEMATCH)){
            String uid = record.value();
            redisTemplate.opsForHash().delete(uid);
        }
        ack.acknowledge();
    }
}

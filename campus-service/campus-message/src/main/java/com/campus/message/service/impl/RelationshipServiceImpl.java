package com.campus.message.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.message.dao.MessageDao;
import com.campus.message.dao.RelationshipDao;
import com.campus.message.domain.Message;
import com.campus.message.domain.Relationship;
import com.campus.message.service.MessageService;
import com.campus.message.service.RelationshipService;
import com.campus.message.socket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RelationshipServiceImpl implements RelationshipService {

    @Autowired
    MessageServiceImpl messageService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RelationshipDao relationshipDao;

    @Autowired
    WebSocket webSocket;

    /**
     * 插入关系
     *
     * @param relationship
     */
    @Override
    public String insert(Relationship relationship) {
        String id = IdWorker.getIdStr(relationship);
        relationship.setRelationshipId(id);
        relationshipDao.insert(relationship);
        return id;
    }
}

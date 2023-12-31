package com.campus.message.socket;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.exception.MysqlException;
import com.campus.common.util.SpringContextUtil;
import com.campus.common.util.TimeUtil;
import com.campus.message.constant.MessageStatus;
import com.campus.message.constant.MessageType;
import com.campus.message.dao.MessageDao;
import com.campus.message.domain.Message;
import com.campus.message.service.MessageService;
import com.campus.message.service.impl.MessageServiceImpl;
import com.campus.message.service.impl.UserOnlineServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import static com.campus.message.constant.MessageType.*;

/**
 * onlineUser 当前登录用户账号
 */
@Service
@Slf4j
@ServerEndpoint("/websocket/{onlineUser}")
public class WebSocket {
    /**
     * 线程安全的无序的集合
     */
    private static final CopyOnWriteArraySet<Session> SESSIONS = new CopyOnWriteArraySet<>();

    /**
     * 存储在线连接数
     */
    private static final Map<String, Session> SESSION_POOL = new HashMap<>();

    private static final String onlineUserKey = "ONLINE_USERS";

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MessageDao messageDao;

    @Autowired
    UserOnlineServiceImpl userOnlineService;


    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 连接成功时
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "onlineUser") String onlineUser) {
        try {
//            if(!SESSION_POOL.containsKey(onlineUser)){
                SESSIONS.add(session);
                SESSION_POOL.put(onlineUser, session);
                ((StringRedisTemplate) SpringContextUtil.getBean(StringRedisTemplate.class)).opsForHash().put(onlineUserKey,onlineUser,TimeUtil.getCurrentTime());
                log.info("【WebSocket消息】有新的连接，总数为：" + SESSIONS.size());
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session ,@PathParam(value = "onlineUser") String onlineUser) {
        try {
            SESSIONS.remove(session);
            SESSION_POOL.remove(onlineUser);
            log.info("【WebSocket消息】连接断开，总数为：" + SESSIONS.size());
            ((StringRedisTemplate) SpringContextUtil.getBean(StringRedisTemplate.class)).opsForHash().delete(onlineUserKey,onlineUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 只对用户在聊天窗口点击【发送】按钮,或者好友请求
     */
    @OnMessage
    public void onMessage(String message, @PathParam("onlineUser") String onlineUser) {
        this.redisTemplate = ((StringRedisTemplate) SpringContextUtil.getBean(StringRedisTemplate.class));
        log.info("【WebSocket消息】" + onlineUser + "发送消息：" + message);
        if (message.equals("ok")) {
            log.info("【WebSocket心跳检测】 用户id : {}", onlineUser);
            MessageService messageService = (MessageService) SpringContextUtil.getBean("messageServiceImpl");
            messageService.setHeartFlag(onlineUser);
            sendOneMessage(onlineUser, "ok");

        } else {
            Message msg = JSONObject.parseObject(message, Message.class);
            MessageService messageService = (MessageService) SpringContextUtil.getBean("messageServiceImpl");
            messageService.sendMessage(msg); // 发送消息
            messageService.updateMessageSession(msg); // 更新会话
        }
    }

    /**
     * 广播消息(系统消息)
     *
     * @param message 消息
     */
    public void sendAllMessage(String message) {
        log.info("【WebSocket消息】广播消息：" + message);
        for (Session session : SESSIONS) {
            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 单点消息
     *
     * @param toUserId 接收者用户
     * @param message  消息
     */
    public void sendOneMessage(String toUserId, String message) {
        /**
         * 分布式
         * 先判断本地是否存储对应的会话
         * */
        RedissonClient redissonClient = ((RedissonClient) SpringContextUtil.getBean("redissonClient"));
        if (isExistSession(toUserId)) { // 存在本地
            Session session = SESSION_POOL.get(toUserId);
            String key = "SESSION_" + toUserId;
            if (message.equals("ok")) {
                RLock rLock = redissonClient.getLock(key);
                try {
                    rLock.tryLock(10, 1, TimeUnit.SECONDS);
                    session.getAsyncRemote().sendText(message); // 消息转发
                    rLock.unlock();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                Message msg = JSONObject.parseObject(message, Message.class);
                if (((UserOnlineServiceImpl) SpringContextUtil.getBean(UserOnlineServiceImpl.class)).isOnline(toUserId)) { // 用户在线
                    log.info(msg.getReceiver() + " 在线");
                    try {
                        RLock rLock = redissonClient.getLock(key);
                        rLock.tryLock(60, 10, TimeUnit.SECONDS);
                        session.getAsyncRemote().sendText(message); // 消息转发
                        String sender = msg.getSender();
                        String receiver = msg.getReceiver();
                        log.info("【WebSocket消息】单点消息：" + message);
                        if (MessageType.of(msg.getType()) == USER) { //用户消息
                            log.info("消息类型为用户消息");
                            //写入对方的消息缓存
                            JSONObject friend = JSONObject.parseObject(String.valueOf(redisTemplate.opsForHash().get("message" + receiver, sender))); // 好友信息
                            if (friend == null) {
                                friend = new JSONObject();
                                friend.put("dialog", new JSONArray());
                            }
                            JSONArray dialog = JSONArray.parseArray(String.valueOf(friend.get("dialog"))); // 聊天内容
                            dialog.add(0, JSONObject.toJSON(msg)); // 在最底部添加聊天内容
                            friend.put("dialog", dialog);
                            redisTemplate.opsForHash().put("message" + receiver, sender, friend.toJSONString()); // 更新redis
                            MessageService messageService = (MessageService) SpringContextUtil.getBean("messageServiceImpl");
                            messageService.updateMessageSession(msg); // 更新会话
                        } else if (MessageType.of(msg.getType()) == REQUEST) { // 请求消息
                            log.info("消息类型为请求消息");
                            //更新对方的请求消息缓存
                            String d = redisTemplate.opsForHash().get("message" + receiver, "request") == null ? "[]" : String.valueOf(redisTemplate.opsForHash().get("message" + receiver, "request"));
                            JSONArray dialog = JSONArray.parseArray(d); // 聊天内容
                            dialog.add(0, JSONObject.toJSON(msg)); // 添加请求内容
                            redisTemplate.opsForHash().put("message" + receiver, "request", dialog.toJSONString()); // 更新redis
                        } else if (MessageType.of(msg.getType()) == SYSTEM) { // 系统消息
                            log.info("消息类型为系统消息");
                            //更新对方的请求消息缓存
                            String s = redisTemplate.opsForHash().get("message" + receiver, "system") == null ? "[]" : String.valueOf(redisTemplate.opsForHash().get("message" + receiver, "system"));
                            JSONArray systemMessage = JSONArray.parseArray(s); // 聊天内容
                            systemMessage.add(0, JSONObject.toJSON(msg)); // 添加请求内容
                            redisTemplate.opsForHash().put("message" + receiver, "system", systemMessage.toJSONString()); // 更新redis
                        }
                        rLock.unlock();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { // 用户不在线
                    log.info(msg.getReceiver() + " 不在线");
                    if (MessageType.of(msg.getType()) == USER) { // 用户消息
                        log.info("消息类型为用户消息");
                        if (redisTemplate.opsForHash().hasKey("autoReply", msg.getReceiver())) { // 有自动回复（离线）
                            log.info("对方设置了自动回复");
                            String replyContent = String.valueOf(redisTemplate.opsForHash().get("autoReply", msg.getReceiver()));
                            Message reply = new Message(); // 设置自动回复消息
                            reply.setSender(msg.getReceiver());
                            reply.setReceiver(msg.getSender());
                            reply.setContent(replyContent);
                            reply.setMessageId(IdWorker.getIdStr(reply));
                            reply.setType(MessageType.AUTOMATIC.code);
                            reply.setDeleted(false);
                            reply.setIsPhoto(false);
                            reply.setStatus(MessageStatus.READ.code);
                            reply.setCreateTime(TimeUtil.getCurrentTime());
                            reply.setUpdateTime(TimeUtil.getCurrentTime());
                            String replyStr = JSONObject.toJSONString(reply);
                            ((MessageDao) SpringContextUtil.getBean(MessageDao.class)).insert(reply);
                            sendOneMessage(msg.getSender(), replyStr); // 自动回复
                        }
                    }
                }
            }
        } else { // 不在本地，则通过消息中间件发送消息到别的服务
            String topic = "WEBSOCKET";
            ListenableFuture<SendResult<String, String>> future = ((KafkaTemplate<String, String>) SpringContextUtil.getBean(KafkaTemplate.class)).send(topic, toUserId, message);
            future.addCallback(result -> log.info("消息成功同步到topic:{} partition:{}", result.getRecordMetadata().topic(), result.getRecordMetadata().partition()),
                    ex -> log.error("消息同步失败，原因：{}", ex.getMessage()));
        }
    }

    /**
     * 多点消息(系统消息)
     *
     * @param userIds 用户编号列表
     * @param message 消息
     */
    public void sendMoreMessage(List<String> userIds, String message) {
        for (String userId : userIds) {
            Session session = SESSION_POOL.get(userId);
            if (session != null && session.isOpen()) {
                try {
                    log.info("【WebSocket消息】多点消息：" + message);
                    session.getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断当前id对应的会话是否存在该服务器本地
     */
    public boolean isExistSession(String userId) {
        return SESSION_POOL.containsKey(userId);
    }

}

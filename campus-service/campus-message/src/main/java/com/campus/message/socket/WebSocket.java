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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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

import static com.campus.message.constant.MessageType.*;

/**
 * onlineUser 当前登录用户账号
 */
@Component
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

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    MessageDao messageDao;

    /**
     * 连接成功时
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "onlineUser") String onlineUser) {
        try {
            SESSIONS.add(session);
            SESSION_POOL.put(onlineUser, session);
            log.info("【WebSocket消息】有新的连接，总数为：" + SESSIONS.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        try {
            SESSIONS.remove(session);
            log.info("【WebSocket消息】连接断开，总数为：" + SESSIONS.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 只对用户在聊天窗口点击【发送】按钮,或者好友请求
     */
    @OnMessage
    public void onMessage(String message, @PathParam("onlineUser") String onlineUser) {
        log.info("【WebSocket消息】" + onlineUser + "发送消息：" + message);
        Message msg = JSONObject.parseObject(message, Message.class);
        MessageService messageService = (MessageService) SpringContextUtil.getBean("messageServiceImpl");
        messageService.sendMessage(msg);
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
        Message msg = JSONObject.parseObject(message, Message.class);
        Session session = SESSION_POOL.get(toUserId);
        if (session != null && session.isOpen()) { // 用户在线
            log.info(msg.getReceiver() + " 在线");
            try {
                synchronized (session) {
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
                }
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
                    messageDao.insert(reply);
                    sendOneMessage(msg.getSender(), replyStr); // 自动回复
                }
            }
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
     * 判断当前用户是否在线
     */
    public boolean isOnline(String userId) {
        Session session = SESSION_POOL.get(userId);
        return session != null && session.isOpen();
    }
}

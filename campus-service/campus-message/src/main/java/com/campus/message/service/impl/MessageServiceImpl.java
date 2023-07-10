package com.campus.message.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.pojo.ServiceData;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.TimeUtil;
import com.campus.message.constant.MessageStatus;
import com.campus.message.constant.MessageType;
import com.campus.message.constant.RelationshipStatus;
import com.campus.message.dao.MessageDao;
import com.campus.message.dao.RelationshipDao;
import com.campus.message.domain.Message;
import com.campus.message.domain.Relationship;
import com.campus.message.feign.UserClient;
import com.campus.message.service.MessageService;
import com.campus.message.socket.WebSocket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MessageServiceImpl implements MessageService {

    @Autowired
    WebSocket webSocket;

    @Autowired
    MessageDao messageDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserClient userClient;

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    RelationshipDao relationshipDao;

    /**
     * 写入消息（系统消息要特殊判断
     *
     * @param message
     */
    @Override
    public boolean insert(Message message) {
        try {

            messageDao.insert(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 发送系统消息
     * 解析消息
     *
     * @param message
     */
    @Override
    public boolean delete(Message message) {
        return false;
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    @Override
    public boolean sendMessage(Message msg) {
        try {
            //初始化消息
            msg.setMessageId(IdWorker.getIdStr(msg));
            msg.setDeleted(false);
            msg.setIsPhoto(false);
            msg.setStatus(MessageStatus.UNREAD.code);
            msg.setCreateTime(TimeUtil.getCurrentTime());
            msg.setUpdateTime(TimeUtil.getCurrentTime());
            //类型判断
            switch (MessageType.of(msg.getType())) { // 根据消息类型进行处理
                case SYSTEM: { // 系统消息
                    String receiver = msg.getReceiver(); // all/id#id#id
                    List<String> receivers = new ArrayList<>();
                    if (receiver.equals("all")) { // 发送全体用户
                        //获取用户列表
                        receivers = (List<String>) userClient.getAllUserId().getData();
                    } else { // 多发
                        receivers = Arrays.asList(receiver.split("#"));

                    }
                    //将消息写入数据库(耗时久，异步）
                    for (int i = 0; i < receivers.size(); i++) {
                        Message message = msg.copy();
                        message.setReceiver(receivers.get(i)); // 修改接收方
                        message.setMessageId(IdWorker.getIdStr(message)); // 修改主键
                        String msgStr = JSONObject.toJSONString(message);
                        //调用套件的异步插入
                        serviceCenter.asyInsert(message);
                        //更新消息列表缓存
                        webSocket.sendOneMessage(receivers.get(i), msgStr);
                    }
                    //更新缓存(管理员）
                    redisTemplate.opsForHash().put("systemMessage", msg.getMessageId(), JSONObject.toJSONString(msg));
                    break;
                }
                case USER: { // 用户消息
                    //写入数据库
                    if (insert(msg)) { // 数据库写入消息成功
                        String sender = msg.getSender();
                        String receiver = msg.getReceiver();
                        String json = JSONObject.toJSONString(msg); // 消息json格式对象
                        //更新消息列表缓存
                        JSONObject friend = JSONObject.parseObject(String.valueOf(redisTemplate.opsForHash().get(sender, receiver))); // 好友信息
                        if (friend == null) {
                            friend = new JSONObject();
                            friend.put("dialog", new JSONArray());
                        }
                        JSONArray dialog = JSONArray.parseArray(String.valueOf(friend.get("dialog"))); // 聊天内容
                        dialog.add(0, JSONObject.toJSON(msg)); // 在最底部添加聊天内容
                        friend.put("dialog", dialog);
                        redisTemplate.opsForHash().put(sender, receiver, friend.toJSONString()); // 更新redis
                        // websocket转发消息
                        webSocket.sendOneMessage(receiver, json);
                    } else {
                        log.info("数据库写入消息错误");
                    }
                    break;
                }
                case REQUEST: {
                    //写入数据库
                    if (insert(msg)) { // 数据库写入消息成功
                        String sender = msg.getSender();
                        String receiver = msg.getReceiver();
                        String json = JSONObject.toJSONString(msg); // 消息json格式对象
                        //更新消息列表缓存
                        String d = redisTemplate.opsForHash().get(sender, "request") == null ? "[]" : String.valueOf(redisTemplate.opsForHash().get(sender, "request"));
                        JSONArray dialog = JSONArray.parseArray(d); // 聊天内容
                        dialog.add(0, JSONObject.toJSON(msg)); // 添加请求内容
                        redisTemplate.opsForHash().put(sender, "request", dialog.toJSONString()); // 更新redis
                        // websocket转发消息
                        webSocket.sendOneMessage(receiver, json);
                    } else {
                        log.info("数据库写入消息错误");
                    }
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 初始化用户消息
     * 将用户相关的数据写入redis(最近50条）
     *
     * @param uid 当前登录用户id
     * @return 是否初始化成功
     */
    @Override
    public boolean initMessage(String uid) {
        return false;
    }


    /**
     * 消息列表懒加载（用户消息/系统消息/请求消息）
     * 从数据库获取对应消息的个数
     * 从数据库中获取对应个数之后的20条数据
     * 将新的消息写入redis
     *
     * @param uid      当前登录用户
     * @param friendId 好友id
     */
    @Override
    public boolean lazyLoadingChatRecords(String uid, String friendId) {
        try{
            String jsonStr = String.valueOf(redisTemplate.opsForHash().get(uid,friendId));
            JSONObject json = JSONObject.parseObject(jsonStr);
            String arrayStr = String.valueOf(json.get("dialog"));
            List<Message> list = JSONArray.parseArray(arrayStr,Message.class);
            int num = list.size(); // 获取已缓存聊天记录个数
            List<Message> loadedMessage = messageDao.lazyLoading(num); // 加载新的数据
            list.addAll(loadedMessage); // 添加新加载的数据
            arrayStr =  JSONArray.toJSONString(list);
            json.put("dialog",arrayStr);
            jsonStr = json.toJSONString();
            redisTemplate.opsForHash().put(uid,friendId,jsonStr);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 接收好友请求（更新消息+添加relationship）（这里不用双删策略，因为该内容只有当前用户可以看到）
     * 更新数据库
     * 更新缓存(当前用户和对方用户）
     * 添加relationship
     * 添加好友缓存（dialog:[],receiver:{……}）
     * 系统发提示消息给发送者
     * 生成新的会话
     *
     * @param uid    当前登录用户
     * @param msgId  消息id
     * @param accept 是否通过好友申请
     */
    @Override
    public boolean handleRequest(String uid, String msgId, Boolean accept) {
        try {
            List<Message> list = JSONArray.parseArray(String.valueOf(redisTemplate.opsForHash().get(uid, "request")), Message.class);
            int index = -1;
            Message msg = new Message(); // 找到msgId对应的消息
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMessageId().equals(msgId)) {
                    index = i;
                    msg = list.get(i);
                    break;
                }
            }
            String sender = msg.getSender();
            if (index != -1) { // 有找到
                if (accept) { // 通过好友申请
                    msg.setStatus(MessageStatus.RECEIVE.code); // 改为接受
                } else {
                    msg.setStatus(MessageStatus.RECEIVE.code); // 改为拒绝
                }
                messageDao.updateById(msg); // 更新数据库
                list.set(index, msg); // 更新列表
                String listStr = JSONArray.toJSONString(list);
                redisTemplate.opsForHash().put(uid, "request", listStr);// 更新缓存
                if (accept) {
                    //新建relationship 写入数据库
                    Relationship rs = new Relationship();
                    rs.setRelationshipId(IdWorker.getIdStr(rs));
                    rs.setSender(sender);
                    rs.setReceiver(msg.getReceiver());
                    rs.setStatus(RelationshipStatus.NORMAL.code);
                    relationshipDao.insert(rs);
                }
                if (redisTemplate.opsForHash().hasKey(sender, "request")) { // 如果对方在线（更新发送者的redis)
                    List<Message> l = JSONArray.parseArray(String.valueOf(redisTemplate.opsForHash().get(sender, "request")), Message.class);
                    int idx = -1;
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).getMessageId().equals(msgId)) {
                            idx = i;
                            break;
                        }
                    }
                    if (idx != -1) { // 更新发送者的redis
                        l.set(idx, msg); // 更新列表
                        String lStr = JSONArray.toJSONString(list);
                        redisTemplate.opsForHash().put(sender, "request", lStr);// 更新缓存
                    }
                }
                //系统发提醒消息给发送者
                String username = uid;
                String content = "";
                Map user = messageDao.getUserDetail(uid); // 当前登录用户的具体信息
                if (user != null) {
                    username = String.valueOf(user.get("username"));
                }
                if (accept) {
                    content = "用户" + username + "已经通过您的好友申请，快去和他聊聊吧 ~ ";

                } else {
                    content = "用户" + username + "已拒绝您的好友申请 ! ";
                }
                sendPromptInformation(sender, content); // 发送提示信息
                if (accept) { // 新建会话
                    Map senderDetail = messageDao.getUserDetail(sender); // 发送者详情
                    JSONObject value = new JSONObject();
                    value.put("dialog", new JSONArray());
                    value.put("user", senderDetail);
                    String valueStr = value.toJSONString();
                    redisTemplate.opsForHash().put(uid, sender, valueStr);
                    if (webSocket.isOnline(sender)) { // 如果对方也在线
                        JSONObject value2 = new JSONObject();
                        value.put("dialog", new JSONArray());
                        value.put("user", user);
                        String valueStr2 = value2.toJSONString();
                        redisTemplate.opsForHash().put(sender, uid, valueStr2);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 系统发消息给普通用户（提醒消息,非管理员发送消息）
     * 在数据库中新增消息
     * 判断对方是否在线，在线则将消息写入对方的缓存
     *
     * @param receiver
     * @param content
     */
    @Override
    public boolean sendPromptInformation(String receiver, String content) {
        Message message = new Message();
        message.setMessageId(IdWorker.getIdStr(message));
        message.setContent(content);
        message.setSender("system");
        message.setReceiver(receiver);
        message.setStatus(MessageStatus.UNREAD.code);
        message.setDeleted(false);
        message.setIsPhoto(false);
        message.setType(MessageType.SYSTEM.code);
        message.setCreateTime(TimeUtil.getCurrentTime());
        message.setUpdateTime(TimeUtil.getCurrentTime());
        if (insert(message)) { // 消息插入成功
            //判断对方是否在线，在线则写入对方的消息缓存
            if (webSocket.isOnline(receiver)) { // 对方在线时
                String s = redisTemplate.opsForHash().get(receiver, "system") == null ? "[]" : String.valueOf(redisTemplate.opsForHash().get(receiver, "system"));
                JSONArray systemMessage = JSONArray.parseArray(s); // 聊天内容
                systemMessage.add(0, JSONObject.toJSON(message)); // 添加请求内容
                redisTemplate.opsForHash().put(receiver, "system", systemMessage.toJSONString()); // 更新redis
            }
            return true;
        }
        return false;
    }

    /**
     * 获取用户聊天详情
     * 将所有聊天记录改为已读
     * 返回redis缓存数据
     *
     * @param uid      当前登录用户id
     * @param friendId 好友id
     */
    @Override
    public JSONObject getUserChatRecords(String uid, String friendId) {
        String jsonStr = String.valueOf(redisTemplate.opsForHash().get(uid,friendId));
        JSONObject json = JSONObject.parseObject(jsonStr);
        String arrayStr = String.valueOf(json.get("dialog"));
        List<Message> array = JSONArray.parseArray(arrayStr,Message.class);
        for (int i = 0; i < array.size(); i++) {
            Message message = array.get(i);
            if(MessageStatus.of(message.getStatus())==MessageStatus.UNREAD){ // 消息未读
                message.setStatus(MessageStatus.READ.code); // 改为已读
                messageDao.updateById(message); // 更新数据库
            }
        }
        arrayStr = JSONArray.toJSONString(array);
        json.put("dialog",arrayStr);
        redisTemplate.opsForHash().put(uid,friendId,json); // 更新缓存
        return json;
    }

}

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
import com.campus.message.pojo.LazyLoadPojo;
import com.campus.message.pojo.User;
import com.campus.message.service.MessageService;
import com.campus.message.socket.WebSocket;
import com.campus.message.vo.InitUserMessageData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
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
    ServiceCenter serviceCenter;

    @Autowired
    UserOnlineServiceImpl userOnlineService;

    @Autowired
    RelationshipDao relationshipDao;


    @Autowired
    RelationshipServiceImpl relationshipService;


    public boolean setHeartFlag(String onlineUser) {
        redisTemplate.opsForValue().set("heart_" + onlineUser, "true", 20, TimeUnit.SECONDS);
        return true;
    }


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
                        receivers = messageDao.getAllUserId();
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
                        JSONObject friend = JSONObject.parseObject(String.valueOf(redisTemplate.opsForHash().get("message" + sender, receiver))); // 好友信息
                        if (friend == null) {
                            friend = new JSONObject();
                            friend.put("dialog", new JSONArray());
                        }
                        JSONArray dialog = JSONArray.parseArray(String.valueOf(friend.get("dialog"))); // 聊天内容
                        dialog.add(0, JSONObject.toJSON(msg)); // 在最底部添加聊天内容
                        friend.put("dialog", dialog);
                        redisTemplate.opsForHash().put("message" + sender, receiver, friend.toJSONString()); // 更新redis
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
                        String d = redisTemplate.opsForHash().get("message" + sender, "request") == null ? "[]" : String.valueOf(redisTemplate.opsForHash().get("message" + sender, "request"));
                        JSONArray dialog = JSONArray.parseArray(d); // 聊天内容
                        dialog.add(0, JSONObject.toJSON(msg)); // 添加请求内容
                        redisTemplate.opsForHash().put("message" + sender, "request", dialog.toJSONString()); // 更新redis
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
     *
     * @param uid 当前登录用户id
     * @return 是否初始化成功
     */
    @Override
    public JSONObject initMessage(String uid) {
        JSONObject ret = new JSONObject(); // 返回值
        //录入所有系统消息(后续不进行懒加载，一次全导入)
        List<Message> systemMessages = messageDao.getSystemMessage(uid);
        redisTemplate.opsForHash().put("message" + uid, "system", JSONArray.toJSONString(systemMessages));
        ret.put("system", systemMessages);
        //录入所有请求消息(后续不进行懒加载，一次全导入)
        List<Message> requestMessages = messageDao.getRequestMessage(uid);
        redisTemplate.opsForHash().put("message" + uid, "request", JSONArray.toJSONString(requestMessages));
        ret.put("request", requestMessages);
        //录入所有用户消息
        List<Message> allDialog = messageDao.getMyAllDialog(uid);
        List<User> friends = getSessionFriends(uid);
        Map<String, List<Message>> rel = new HashMap<>();
        for (int i = 0; i < friends.size(); i++) {
            String friendId = String.valueOf(friends.get(i).getUserId());
            rel.put(friendId, new ArrayList<Message>()); // 保证map值不为空

        }
        for (int i = 0; i < allDialog.size(); i++) {
            Message message = allDialog.get(i);
            String friendId = null;
            String receiver = message.getReceiver();
            String sender = message.getSender();
            if (uid.equals(sender)) { // 作为发送者
                friendId = receiver;
            }
            if (uid.equals(receiver)) { // 作为发送者
                friendId = sender;
            }
            if (friendId != null && rel.containsKey(friendId)) {
                rel.get(friendId).add(message);
            }

        }
        for (User friend : friends) {
            JSONObject jsonContent = new JSONObject();
            String friendId = String.valueOf(friend.getUserId());
            jsonContent.put("user", friend);
            jsonContent.put("dialog", rel.get(friendId));
            redisTemplate.opsForHash().put("message" + uid, friendId, jsonContent.toJSONString());
            ret.put(friendId, jsonContent);
        }
        //删除自动回复
        if (redisTemplate.opsForHash().hasKey("autoReply", uid)) {
            redisTemplate.opsForHash().delete("autoReply", uid);
        }
        return ret;
    }


    /**
     * 【首页】-》【消息】
     */
    public List<InitUserMessageData> clickMyMessage(String uid) {
        List<InitUserMessageData> ret = new ArrayList<>();
        final String sessionList = redisTemplate.opsForValue().get("session" + uid);
        if (sessionList == null) { // 没有缓存，从数据库中获取数据，并添加到缓存中
            List<Message> allDialog = messageDao.getMyAllDialog(uid); // 获取所有聊天内容(限300条)
            List<User> sessionFriends = getSessionFriends(uid); // 获取会话好友列表（包括临时好友）
            Map<String, String> recentContent = new HashMap<>();
            Map<String, String> recentTime = new HashMap<>();
            for (int i = 0; i < sessionFriends.size(); i++) {
                String friendId = String.valueOf(sessionFriends.get(i).getUserId());
                recentContent.put(friendId, ""); // 保证map值不为空
            }
            for (int i = 0; i < allDialog.size(); i++) {
                Message message = allDialog.get(i);
                String friendId = null;
                String content = message.getContent();
                String time = message.getCreateTime();
                String receiver = message.getReceiver();
                String sender = message.getSender();
                if (uid.equals(sender)) { // 作为发送者
                    friendId = receiver;
                }
                if (uid.equals(receiver)) { // 作为发送者
                    friendId = sender;
                }
                if (friendId != null && recentContent.containsKey(friendId) && recentContent.get(friendId).length() == 0) { // 只保留第一次，第一次就是最新的
                    recentContent.put(friendId, content);
                    recentTime.put(friendId, time);
                }
            }
            for (User friend : sessionFriends) {
                InitUserMessageData data = new InitUserMessageData();
                data.setUserId(friend.getUserId());
                data.setUsername(friend.getUsername());
                data.setUserImage(friend.getUserImage());
                data.setRecentContent(recentContent.get(friend.getUserId()));
                if (recentContent.get(friend.getUserId()).length() > 0) {
                    data.setRecentTime(recentTime.get(friend.getUserId()));
                    ret.add(data);
                } else {
                    data.setRecentTime("");
                }
            }
            Collections.sort(ret);
            //保存到缓存中
            redisTemplate.opsForValue().set("session" + uid, JSONArray.toJSONString(ret));
            return ret;
        } else { // 有缓存-》直接将缓存结果返回
            String arrayStr = redisTemplate.opsForValue().get("session" + uid);
            if (arrayStr != null && arrayStr.length() > 0) {
                return JSONArray.parseArray(arrayStr, InitUserMessageData.class);
            }
            return new ArrayList<>();
        }
    }


    /**
     * 用户点击首页下的【消息】，弹出的好友列表
     * 返回的数据结构：
     * {
     * userId:{}
     * }
     *
     * @param uid 当前登录用户id
     * @return 是否初始化成功
     */
    @Override
    public List<InitUserMessageData> initUserMessage(String uid) {
        List<InitUserMessageData> ret = new ArrayList<>();
        List<Message> allDialog = messageDao.getMyAllDialog(uid); // 获取所有聊天内容(限300条)
        List<User> sessionFriends = getSessionFriends(uid); // 获取会话好友列表（包括临时好友）
        Map<String, String> recentContent = new HashMap<>();
        Map<String, String> recentTime = new HashMap<>();
        for (int i = 0; i < sessionFriends.size(); i++) {
            String friendId = String.valueOf(sessionFriends.get(i).getUserId());
            recentContent.put(friendId, ""); // 保证map值不为空
        }
        for (int i = 0; i < allDialog.size(); i++) {
            Message message = allDialog.get(i);
            String friendId = null;
            String content = message.getContent();
            String time = message.getCreateTime();
            String receiver = message.getReceiver();
            String sender = message.getSender();
            if (uid.equals(sender)) { // 作为发送者
                friendId = receiver;
            }
            if (uid.equals(receiver)) { // 作为发送者
                friendId = sender;
            }
            if (friendId != null && recentContent.containsKey(friendId) && recentContent.get(friendId).length() == 0) { // 只保留第一次，第一次就是最新的
                recentContent.put(friendId, content);
                recentTime.put(friendId, time);
            }
        }
        for (User friend : sessionFriends) {
            InitUserMessageData data = new InitUserMessageData();
            data.setUserId(friend.getUserId());
            data.setUsername(friend.getUsername());
            data.setUserImage(friend.getUserImage());
            data.setRecentContent(recentContent.get(friend.getUserId()));
            if (recentContent.get(friend.getUserId()).length() > 0) {
                data.setRecentTime(recentTime.get(friend.getUserId()));
            } else {
                data.setRecentTime("");
            }
            ret.add(data);
        }
        Collections.sort(ret);
        return ret;
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
        try {
            String jsonStr = String.valueOf(redisTemplate.opsForHash().get("message" + uid, friendId));
            JSONObject json = JSONObject.parseObject(jsonStr);
            if (json.get("dialog") == null) {
                json.put("dialog", new JSONArray());
            }
            String arrayStr = String.valueOf(json.get("dialog"));
            List<Message> list = JSONArray.parseArray(arrayStr, Message.class);
            int num = list.size(); // 获取已缓存聊天记录个数
            List<Message> loadedMessage = messageDao.lazyLoading(new LazyLoadPojo(uid, friendId, num)); // 加载新的数据
            list.addAll(loadedMessage); // 添加新加载的数据
            arrayStr = JSONArray.toJSONString(list);
            json.put("dialog", arrayStr);
            jsonStr = json.toJSONString();
            redisTemplate.opsForHash().put("message" + uid, friendId, jsonStr);
            return true;
        } catch (Exception e) {
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
            List<Message> list = JSONArray.parseArray(String.valueOf(redisTemplate.opsForHash().get("message" + uid, "request")), Message.class);
            int index = -1;
            Message msg = new Message(); // 找到msgId对应的消息
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMessageId().equals(msgId)) {
                    index = i;
                    msg = list.get(i);
                    break;
                }
            }
            String sender = msg.getSender(); // （发起好友申请）者
            if (index != -1) { // 有找到
                if (accept) { // 通过好友申请
                    msg.setStatus(MessageStatus.RECEIVE.code); // 改为接受
                } else {
                    msg.setStatus(MessageStatus.RECEIVE.code); // 改为拒绝
                }
                messageDao.updateById(msg); // 更新数据库
                list.set(index, msg); // 更新列表
                String listStr = JSONArray.toJSONString(list);
                redisTemplate.opsForHash().put("message" + uid, "request", listStr);// 更新缓存
                if (accept) {
                    //新建relationship 写入数据库
                    Relationship rs = new Relationship();
                    rs.setRelationshipId(IdWorker.getIdStr(rs));
                    rs.setSender(sender);
                    rs.setReceiver(msg.getReceiver());
                    rs.setStatus(RelationshipStatus.NORMAL.code);
                    relationshipService.addFriend(rs);// 插入数据
                }
                if (redisTemplate.opsForHash().hasKey("message" + sender, "request")) { // 如果对方在线（更新发送者的redis)
                    List<Message> l = JSONArray.parseArray(String.valueOf(redisTemplate.opsForHash().get("message" + sender, "request")), Message.class);
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
                        redisTemplate.opsForHash().put("message" + sender, "request", lStr);// 更新缓存
                    }
                }
                //系统发提醒消息给发送者
                String username = uid;
                String content = "";
                User user = messageDao.getUserDetail(uid); // 当前登录用户的具体信息
                if (user != null) {
                    username = String.valueOf(user.getUsername());
                }
                if (accept) {
                    content = "用户" + username + "已经通过您的好友申请，快去和他聊聊吧 ~ ";

                } else {
                    content = "用户" + username + "已拒绝您的好友申请 ! ";
                }
                sendPromptInformation(sender, content); // 发送提示信息
                if (accept) { // 新建会话
                    User senderDetail = messageDao.getUserDetail(sender); // 发送者详情
                    JSONObject value = new JSONObject();
                    value.put("dialog", new JSONArray());
                    value.put("user", senderDetail);
                    String valueStr = value.toJSONString();
                    redisTemplate.opsForHash().put("message" + uid, sender, valueStr);
                    if (userOnlineService.isOnline(sender)) { // 如果对方也在线
                        JSONObject value2 = new JSONObject();
                        value2.put("dialog", new JSONArray());
                        value2.put("user", user);
                        String valueStr2 = value2.toJSONString();
                        redisTemplate.opsForHash().put("message" + sender, uid, valueStr2);
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
            if (userOnlineService.isOnline(receiver)) { // 对方在线时
                String s = redisTemplate.opsForHash().get("message" + receiver, "system") == null ? "[]" : String.valueOf(redisTemplate.opsForHash().get("message" + receiver, "system"));
                JSONArray systemMessage = JSONArray.parseArray(s); // 聊天内容
                systemMessage.add(0, JSONObject.toJSON(message)); // 添加请求内容
                redisTemplate.opsForHash().put("message" + receiver, "system", systemMessage.toJSONString()); // 更新redis
                webSocket.sendOneMessage(receiver, JSONObject.toJSONString(message));
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
        String jsonStr = String.valueOf(redisTemplate.opsForHash().get("message" + uid, friendId));
        JSONObject json = JSONObject.parseObject(jsonStr);
        if (json.get("dialog") == null) {
            json.put("dialog", new JSONArray());
        }
        String arrayStr = String.valueOf(json.get("dialog"));
        List<Message> array = JSONArray.parseArray(arrayStr, Message.class);
        for (int i = 0; i < array.size(); i++) {
            Message message = array.get(i);
            if (MessageStatus.of(message.getStatus()) == MessageStatus.UNREAD) { // 消息未读
                message.setStatus(MessageStatus.READ.code); // 改为已读
                messageDao.updateById(message); // 更新数据库
                if (userOnlineService.isOnline(friendId)) { // 如果好友在线，则修改好友的聊天缓存
                    changeDialog(friendId, uid, message.getMessageId(), "status", MessageStatus.READ);
                }
            }
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONArray.toJSONString(array));
        json.put("dialog", jsonArray);
        redisTemplate.opsForHash().put("message" + uid, friendId, json.toJSONString()); // 更新缓存
        return json;
    }

    /**
     * 清理缓存
     * 删除当前用户对应的消息缓存空间
     * 如果当前用户有设置自动回复内容，则将其添加到自动回复缓存中
     *
     * @param uid 当前登陆用户
     */
    @Override
    public boolean clearCache(String uid) {
        redisTemplate.delete(uid); // 删除当前用户对应的消息缓存空间
        String auto = messageDao.getAutoReply(uid);
        if (auto != null && auto.length() != 0) { // 有设置自动回复内容
            redisTemplate.opsForHash().put("autoReply", uid, auto);
        }
        return true;
    }

    @Override
    public boolean clearUnRead(String uid) {
        try {
            //清除缓存
            clearCache(uid);
            messageDao.clearUnRead(uid); // 修改数据库
            initMessage(uid); // 重新加载数据
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public List<User> getSessionFriends(String uid) {
        return relationshipDao.getSessionFriends(uid); // 获取会话好友列表
    }

    /**
     * 更新session
     * <p>
     * 判断消息的双方是否在线，
     * 若在线，则更新其session缓存
     * 若不在线，待其下一次登录时从数据库获取数据则会自动更新session
     *
     * @param message
     */
    @Override
    public boolean updateMessageSession(Message message) {
        String sender = message.getSender();
        String receiver = message.getReceiver();
        String senderSession = redisTemplate.opsForValue().get("session" + sender);
        String receiverSession = redisTemplate.opsForValue().get("session" + receiver);
        if(senderSession!=null&&senderSession.length()>1){ // 存在缓存，即处于登录状态
            List<InitUserMessageData> list1 = JSONArray.parseArray(senderSession,InitUserMessageData.class);
            for (int i = 0; i < list1.size(); i++) {
                if(list1.get(i).getUserId().equals(receiver)){ // 找到对应的位置
                    InitUserMessageData data = list1.remove(i);
                    data.setRecentContent(message.getContent());
                    data.setRecentTime(TimeUtil.getCurrentTime());
                    list1.add(0,data);
                    redisTemplate.opsForValue().set("session" + sender,JSONArray.toJSONString(list1));
                    break;
                }
            }
        }
        if(receiverSession!=null&&receiverSession.length()>1){ // 存在缓存，即处于登录状态
            List<InitUserMessageData> list2 = JSONArray.parseArray(receiverSession,InitUserMessageData.class);
            for (int i = 0; i < list2.size(); i++) {
                if(list2.get(i).getUserId().equals(sender)){ // 找到对应的位置
                    InitUserMessageData data = list2.remove(i);
                    data.setRecentContent(message.getContent());
                    data.setRecentTime(TimeUtil.getCurrentTime());
                    list2.add(0,data);
                    redisTemplate.opsForValue().set("session" + receiver,JSONArray.toJSONString(list2));
                    break;
                }
            }
        }
        return true;
    }


    /**
     * 修改好友聊天数据
     */
    private boolean changeDialog(String uid, String friendId, String msgId, String arg, Object argValue) {
        String jsonStr = String.valueOf(redisTemplate.opsForHash().get("message" + uid, friendId));
        JSONObject json = JSONObject.parseObject(jsonStr);
        if (json.get("dialog") == null) {
            json.put("dialog", new JSONArray());
        }
        String arrayStr = String.valueOf(json.get("dialog"));
        JSONArray array = JSONArray.parseArray(arrayStr);
        for (Object o : array) {
            JSONObject msg = JSONObject.parseObject(String.valueOf(o));
            msg.put(arg, argValue);
            break;
        }
        return true;
    }

}

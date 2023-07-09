package com.campus.message.service;

import com.campus.message.constant.MessageType;
import com.campus.message.domain.Message;

import java.util.List;


/**
 * 消息只有添加和逻辑删除，没有更新
 * 增量缓存
 * 用户登录时
 * */
public interface MessageService {

    /**
     * 写入消息（系统消息要特殊判断
     * */
    boolean insert(Message message);



    /**
     * 发送系统消息
     * 解析消息
     * */
    boolean delete(Message message);

    /**
     * 发送消息
     * 系统消息
     * */
    boolean sendMessage(Message msg);

    /**
     * 初始化用户消息
     * 将用户相关的数据写入redis(最近50条）
     * @param uid 当前登录用户id
     * @return 是否初始化成功
     * */
    boolean initMessage(String uid);

    /**
     * 对象消息懒加载
     * 从数据库获取新的消息
     * 将新的消息写入redis
     * @param sender 发送者（当前登录用户）
     * @param receiver 接收者（聊天对象）
     * @return 新添加的50条记录
     * */
    List<Message> lazyLoading(String sender,String receiver);

    /**
     * 消息列表懒加载（用户消息/系统消息/请求消息）
     * 从数据库获取新的消息
     * 将新的消息写入redis
     * @param sender 发送者（当前登录用户）
     * @return 新添加的10条记录
     * */
    List<Message> lazyLoading(String sender,MessageType type);



    /**
     * 接收好友请求（更新消息+添加relationship）
     * 删除缓存
     * 更新数据库
     * 再次删除缓存
     * 添加relationship
     * 添加好友缓存（dialog:[],receiver:{……}）
     * 系统发消息给发送者
     * */
    boolean acceptRequest(String uid,String msgId,Boolean accept);


    /**
     * 系统发消息给普通用户（提醒消息,非管理员发送消息）
     * */
    boolean sendPromptInformation(String receiver,String content);
}

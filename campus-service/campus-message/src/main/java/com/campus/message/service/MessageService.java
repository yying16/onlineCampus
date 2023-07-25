package com.campus.message.service;

import com.alibaba.fastjson.JSONObject;
import com.campus.message.constant.MessageType;
import com.campus.message.domain.Message;
import com.campus.message.vo.InitUserMessageData;

import java.util.List;


/**
 * 消息只有添加和逻辑删除，没有更新
 * 增量缓存
 * 用户登录时
 */
public interface MessageService {

    /**
     * 写入消息（系统消息要特殊判断
     */
    boolean insert(Message message);


    /**
     * 删除消息
     */
    boolean delete(Message message);

    /**
     * 发送消息
     * 系统消息
     */
    boolean sendMessage(Message msg);

    /**
     * 初始化用户消息
     * 将用户相关的数据写入redis(最近50条）
     *
     * @param uid 当前登录用户id
     * @return 是否初始化成功
     */
    JSONObject initMessage(String uid);


    /**
     * 初始化用户消息
     * 将用户相关的数据写入redis(最近50条）
     *
     * @param uid 当前登录用户id
     * @return 是否初始化成功
     */
    List<InitUserMessageData> initUserMessage(String uid);


    /**
     * 消息列表懒加载（用户消息/系统消息/请求消息）
     * 从数据库获取对应消息的个数
     * 从数据库中获取对应个数之后的20条数据
     * 将新的消息写入redis
     *
     * @param uid 当前登录用户
     * @param friendId 好友id
     *
     */
    boolean lazyLoadingChatRecords(String uid,String friendId);


    /**
     * 接收好友请求（更新消息+添加relationship）
     * 删除缓存
     * 更新数据库
     * 再次删除缓存
     * 添加relationship
     * 添加好友缓存（dialog:[],receiver:{……}）
     * 系统发消息给发送者
     */
    boolean handleRequest(String uid, String msgId, Boolean accept);


    /**
     * 系统发消息给普通用户（提醒消息,非管理员发送消息）
     */
    boolean sendPromptInformation(String receiver, String content);

    /**
     * 获取用户聊天详情
     * <p>
     * 返回redis缓存数据
     *
     * @param uid      当前登录用户id
     * @param friendId 好友id
     */
    JSONObject getUserChatRecords(String uid, String friendId);

    /**
     * 清理缓存
     * 删除当前用户对应的消息缓存空间
     * 如果当前用户有设置自动回复内容，则将其添加到自动回复缓存中
     *
     * @param uid 当前登陆用户
     * */
    boolean clearCache(String uid);


    /**
     * 清除未读
     * 删除缓存
     * 修改数据库
     * 再删除缓存
     * 重新添加缓存
     * */
    boolean clearUnRead(String uid);
}

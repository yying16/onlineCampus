package com.campus.message.service;

public interface MatchService {

    /**
     * 加入匹配列表
     *
     * 发送消息到中间件
     * */
    boolean match(String uid);


    /**
     * 取消匹配
     *
     * 发送取消消息到中间件
     * */
    boolean cacheMatch(String uid);
}

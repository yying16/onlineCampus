package com.campus.message.service;

public interface UserOnlineService {

    /**
     * 判断用户是否在线
     * */
    boolean isOnline(String uid);
}

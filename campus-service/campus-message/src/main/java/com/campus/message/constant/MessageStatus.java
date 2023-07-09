package com.campus.message.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 消息状态(0-未读，1-已读,2-异常)
 */
@AllArgsConstructor
@Getter
public enum MessageStatus {
    UNREAD(0, "未读"),
    READ(1, "已读"),
    ABNORMAL(2, "异常"),
    REFUSE(3, "拒绝"),
    RECEIVE(4, "接受");

    public Integer code;
    public String msg;

    public static MessageStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}
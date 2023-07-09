package com.campus.message.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 0-用户消息，1-系统消息，2-请求消息，3-自动回复消息
 */
@AllArgsConstructor
@Getter
public enum MessageType {
    USER(0, "用户消息"),
    SYSTEM(1, "系统消息"),
    REQUEST(2, "请求消息"),
    AUTOMATIC(3, "自动回复消息");

    public Integer code;
    public String msg;

    public static MessageType of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

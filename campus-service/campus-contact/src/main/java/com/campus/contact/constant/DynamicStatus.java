package com.campus.contact.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * author yying
 * 0-公开/1-部分好友可见/2-私有
 */
@AllArgsConstructor
@Getter
public enum DynamicStatus {
    PUBLIC(0, "公开"),
    PORTION(1, "部分好友可见"),
    PRIMARY(2, "仅自己可见");

    public Integer code;
    public String msg;


    public static DynamicStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

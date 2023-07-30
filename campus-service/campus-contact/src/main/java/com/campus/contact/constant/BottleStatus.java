package com.campus.contact.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

@AllArgsConstructor
@Getter
public enum BottleStatus {
    NORMAL(0, "正常"),
    CLOSE(1, "关闭"),
    ABNORMAL(2, "异常");

    public Integer code;
    public String msg;


    public static BottleStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }

}

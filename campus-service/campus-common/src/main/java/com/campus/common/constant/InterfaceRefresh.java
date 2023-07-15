package com.campus.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 前端界面刷新信息常量汇总
 * */
@AllArgsConstructor
@Getter
public enum InterfaceRefresh {
    REFRESH_MAIL("REFRESH_MAIL","用户进行邮箱绑定成功后前端界面刷新邮箱");

    public String code;
    public String msg;

    public static InterfaceRefresh of(String code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

package com.campus.recruit.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 发布状态(0-发布中，1-已结束,2-已关闭)
 */
@AllArgsConstructor
@Getter
public enum RecruitStatus {
    RELEASING(0, "发布中"),
    FINISHED(1, "已结束"),
    CLOSED(2, "已关闭");

    public Integer code;
    public String msg;

    public static RecruitStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}
package com.campus.parttime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 工作状态（0-招满 1-关闭 2-开启）
 */
@AllArgsConstructor
@Getter
public enum JobStatus {
    FULL(0,"招满"),
    CLOSE(1,"关闭"),
    OPEN(2,"开启");

    public Integer code;
    public String msg;
    public static JobStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

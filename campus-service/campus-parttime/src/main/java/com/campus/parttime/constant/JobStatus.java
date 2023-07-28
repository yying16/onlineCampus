package com.campus.parttime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 工作状态（0-开启 1-关闭 2-招满）
 */
@AllArgsConstructor
@Getter
public enum JobStatus {
    OPEN(0,"开启"),
    CLOSE(1,"关闭"),
    FULL(2,"招满");

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

package com.campus.parttime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 兼职申请的状态（0-已申请，1-已通过，2-已完成，3-已拒绝）
 */
@AllArgsConstructor
@Getter
public enum ApplyStatus {
    APPLIED(0,"已申请"),
    PASSED(1,"已通过"),
    REFUSED(2,"已拒绝");

    public Integer code;
    public String msg;
    public static ApplyStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }

}

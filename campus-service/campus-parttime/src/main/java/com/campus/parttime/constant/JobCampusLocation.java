package com.campus.parttime.constant;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 兼职所在校区（0-佛山校区 1-广州校区）
 */
@AllArgsConstructor
@Getter
public enum JobCampusLocation {
    FOSHAN(0,"佛山校区"),
    GUANGZHOU(1,"广州校区");

    public Integer code;
    public String msg;
    public static JobCampusLocation of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}


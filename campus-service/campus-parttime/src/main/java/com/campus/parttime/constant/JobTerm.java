package com.campus.parttime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 兼职任期(0-短期，1-长期)
 */
@AllArgsConstructor
@Getter
public enum JobTerm {
    SHORT(0,"短期"),
    LONG(1,"长期");

    public Integer code;
    public String msg;
    public static JobTerm of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

package com.campus.message.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 关系状态(0-正常关系，1-屏蔽,-1 -删除)
 */
@AllArgsConstructor
@Getter
public enum RelationshipStatus {
    NORMAL(0, "正常关系"),
    BLOCK(1, "屏蔽"),
    DELETED(-1, "删除");

    public Integer code;
    public String msg;

    public static RelationshipStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

package com.campus.parttime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 订单执行状态（0-进行中，1-已完成，2-已超时，3-已取消，4-确认完成）
 */
@AllArgsConstructor
@Getter

public enum OperationStatus {
    ACTIVE(0,"进行中"),
    COMPLETED(1,"已完成"),
    TIMEOUT(2,"已超时"),
    CANCEL(3,"已取消"),
    CONFIRM(4,"确认完成");

    public Integer code;
    public String msg;
    public static OperationStatus of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

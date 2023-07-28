package com.campus.parttime.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
 */
@AllArgsConstructor
@Getter
public enum JobClassification {
    BUY(0,"代购"),
    ERRAND(1,"跑腿"),
    STUDY(2,"学习"),
    PROMOTION(3,"宣传"),
    TECH(4,"技术"),
    TUTOR(5,"家教"),
    AIDE(6,"助理"),
    OTHER(7,"其他");

    public Integer code;
    public String msg;
    public static JobClassification of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }
}

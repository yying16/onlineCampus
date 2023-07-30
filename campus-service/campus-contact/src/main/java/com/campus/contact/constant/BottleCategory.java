package com.campus.contact.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 分类(0-其他,1-交友,2-表白，3-问答，4-搞笑，5-心灵鸡汤，6-吐槽，7-美食，
 * 8-生活，9-旅游，10-健康，11-文化，12-技能，13-科普，14-祝福)
 * */
@AllArgsConstructor
@Getter
public enum BottleCategory {
    OTHER(0, "其他"),
    FRIEND(1, "交友"),
    EXPRESS(2, "表白"),
    QUESTION(3, "问答"),
    FUNNY(4, "搞笑"),
    SOUL(5, "心灵鸡汤"),
    RANT(6, "吐槽"),
    DELICACY(7, "美食"),
    LIFE(8, "生活"),
    TRAVEL(9, "旅游"),
    HEALTH(10, "健康"),
    CULTURE(11, "文化"),
    SKILL(12, "技能"),
    SCIENCE(13, "科普"),
    BLESS(14, "祝福");

    public Integer code;
    public String msg;


    public static BottleCategory of(Integer code) {
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        () -> new IllegalArgumentException(code + " not exists")
                );
    }

}

package com.campus.contact.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddBottleForm {
    String promulgatorId; // 发布者id
    String content; // 漂流瓶内容
    List<String> images; // 图片
    String category; // 分类(0-其他,1-交友,2-表白，3-问答，4-搞笑，5-心灵鸡汤，6-吐槽，7-美食，8-生活，9-旅游，10-健康，11-文化，12-技能，13-科普，14-祝福)
}

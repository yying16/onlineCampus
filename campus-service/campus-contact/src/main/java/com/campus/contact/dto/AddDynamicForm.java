package com.campus.contact.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDynamicForm {
    String promulgatorId;//发布者账号(用户获取用户详情)
    String promulgatorName;//发布者昵称(冗余数据)
    String promulgatorImage; // 发布者头像(冗余数据)
    String content;//文案
    List<String> photos = new ArrayList<>();//图片url集合
    String city;//城市
    String address; // 地理位置信息
    List<String> label; // 标签列表
    Integer status;//状态(0-公开/1-部分好友可见/2-私有)
    List<String> targets; // 可查看列表（当status为部分好友可见有效）

}

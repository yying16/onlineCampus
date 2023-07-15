package com.campus.contact.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * author yying
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private String id;
    private String _class;
    private String content; // 评论内容
    private String promulgatorId; // 发布者id
    private String promulgatorName; // 发布者名称（冗余数据）
    private Boolean deleted = false; // 删除标志
    private String createTime; // 创建时间
    private String updateTime; // 更新时间
    private String parent; //父级评论
    private List<Comment> comments = new ArrayList<>(); // 子评论
}
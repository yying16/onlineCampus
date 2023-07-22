package com.campus.contact.service;

import com.campus.contact.domain.Comment;

import java.util.List;

public interface CommentService {

//    /**
//     * 插入直接下级评论
//     * @param superId 上级评论id
//     * @param comment 直接下级评论
//     * */
//    public List<Comment> insertComment(String superId, Comment comment);
//
//    /**
//     * 删除评论
//     * */
//    public List<Comment> deleteComment(String id);

    /**
     * 添加评论
     * @param dynamicId 动态id
     * @param comment 要添加的评论
     * */
    public boolean addComment(String dynamicId,Comment comment);

}

package com.campus.contact.service;

import com.campus.contact.domain.Comment;
import com.campus.contact.domain.Dynamic;

import java.util.List;

public interface DynamicService {
    /**
     * 插入Dynamic
     */
    public String insertDynamic(Dynamic entity);

    /**
     * 查找Dynamic
     *
     * @param content 搜索内容
     */
    public List<Dynamic> searchDynamic(String content);

    /**
     * 查找同城
     *
     * @param city 当前用户城市
     */
    public List<Dynamic> searchCityWide(String city);

    /**
     * 查看我的动态
     * */
    public List<Dynamic> searchOnesDynamic(String userId);

    /**
     * 查看Dynamic详情
     *
     * @param id Dynamic的id
     */
    public Dynamic detail(String id);


    /**
     * 逻辑删除Dynamic
     *
     * @param id Dynamic的id
     */
    public long deleteDynamicById(String dynamicId);

    /**
     * 插入直接下级评论
     *
     * @param _id     当前动态_id
     * @param comment 直接下级评论
     */
    public String insertComment(String _id, Comment comment);

    /**
     * 删除直接下级评论
     *
     * @param dynamicId 动态id
     * @param commentId 一级评论id
     */
    public long deleteComment(String dynamicId, String commentId);

    /**
     * 点赞
     *
     * @param _id    当前动态_id
     * @param userId 点赞用户id
     */
    public long insertLike(String _id, String userId,String username);

    /**
     * 取消点赞
     *
     * @param dynamicId    当前动态_id
     * @param userId 点赞用户id
     */
    public long deleteLike(String dynamicId, String userId,String userName);


    /**
     * 获取dynamic下的所有comment
     * */
    public List<Comment> getComments(String dynamicId);

    public void updateDynamic(String dynamicId);
}

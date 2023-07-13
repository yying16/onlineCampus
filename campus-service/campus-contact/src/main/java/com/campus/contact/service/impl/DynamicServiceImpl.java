package com.campus.contact.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.util.TimeUtil;
import com.campus.contact.dao.DynamicDao;
import com.campus.contact.domain.Comment;
import com.campus.contact.domain.Dynamic;
import com.campus.contact.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class DynamicServiceImpl implements DynamicService {

    @Autowired
    DynamicDao dynamicDao;

    @Override
    public String insertDynamic(Dynamic entity) {
        entity.setDeleted(false);
        entity.setCreateTime(TimeUtil.getCurrentTime());
        entity.setUpdateTime(TimeUtil.getCurrentTime());
        return dynamicDao.insert(entity);
    }

    @Override
    public List<Dynamic> searchDynamic(String content) {
        Criteria criteria = Criteria.where("content").regex(".*" + content + ".*");
        return dynamicDao.search(criteria);
    }

    @Override
    public List<Dynamic> searchCityWide(String city) {
        Criteria criteria = Criteria.where("city").is(city);
        return dynamicDao.search(criteria);
    }

    @Override
    public List<Dynamic> searchOnesDynamic(String userId) {
        Criteria criteria = Criteria.where("promulgatorId").is(userId);
        return dynamicDao.search(criteria);
    }

    @Override
    public Dynamic detail(String id) {
        return dynamicDao.detail(id);
    }

    @Override
    public String insertComment(String dynamicId, Comment comment) {
        updateDynamic(dynamicId);
        comment.setCreateTime(TimeUtil.getCurrentTime());
        comment.setUpdateTime(TimeUtil.getCurrentTime());
        comment.setId(dynamicId + "#" + IdWorker.getIdStr(comment));//设置uuid
        dynamicDao.insertSubList(dynamicId, "comments", comment);
        return comment.getId();
    }

    @Override
    public long deleteComment(String dynamicId, String commentId) {
        updateDynamic(dynamicId);
        Dynamic dynamic = detail(dynamicId);
        List<Comment> list = dynamic.getComments();
        Comment comment = null;
        for (int i = 0; i < list.size(); i++) {
            if (String.valueOf(list.get(i).getId()).equals(commentId)) {
                comment = list.get(i);
                break;
            }
        }
        if (comment == null) {
            return 0L;
        }
        return dynamicDao.deleteSubList(dynamicId, "comments", comment);
    }

    public List<Comment> getComments(String dynamicId) {
        return dynamicDao.getComments(dynamicId);
    }

    @Override
    public long insertLike(String dynamicId, String userId, String username) {
        updateDynamic(dynamicId);
        dynamicDao.insertSubList(dynamicId, "likeId", userId);
        return dynamicDao.insertSubList(dynamicId, "likeName", username);
    }

    @Override
    public long deleteLike(String dynamicId, String userId, String userName) {
        updateDynamic(dynamicId);
        dynamicDao.deleteSubList(dynamicId, "likeId", userId);
        return dynamicDao.deleteSubList(dynamicId, "likeName", userName);
    }

    @Override
    public long deleteDynamicById(String dynamicId) {
        updateDynamic(dynamicId);
        return dynamicDao.update(dynamicId, new HashMap() {{
            put("deleted", true);
        }});
    }

    public void updateDynamic(String dynamicId) {
        dynamicDao.update(dynamicId, new HashMap() {{
            put("updateTime", TimeUtil.getCurrentTime());
        }});
    }
}

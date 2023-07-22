package com.campus.contact.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.contact.dao.DynamicDao;
import com.campus.contact.domain.Comment;
import com.campus.contact.domain.Dynamic;
import com.campus.contact.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    DynamicServiceImpl dynamicService;

    @Autowired
    DynamicDao dynamicDao;

//    @Override
//    public List<Comment> insertComment(String superId, Comment comment) {
//        String[] ids = superId.split("#");
//        StringBuilder commentId = new StringBuilder(ids[0]);
//        List<Comment> List = dynamicService.getComments(ids[0]);
//        List<Comment> list = List;
//        for (int i = 1; i <ids.length ; i++) {
//            commentId.append("#").append(ids[i]);
//            Comment entity = null;
//            for (int j = 0; j < list.size(); j++) {
//                if(list.get(j).getId().equals(commentId.toString())){
//                    entity = list.get(j);
//                    break;
//                }
//            }
//            assert entity != null;
//            if(entity.getComments()==null){
//                entity.setComments(new ArrayList<Comment>());
//            }
//            list = entity.getComments();
//        }
//        comment.setId(superId+"#"+ IdWorker.getIdStr(Comment.class));
//        Date now = new Date();
//        DateUtil.offset(now, DateField.HOUR, 8);
//        comment.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
//        comment.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(now));
//        list.add(comment);
//        dynamicDao.updateComment(ids[0],List); // 刷新评论区
//        return List;
//    }
//
//    @Override
//    public List<Comment> deleteComment(String id) {
//        String[] ids = id.split("#");
//        StringBuilder commentId = new StringBuilder(ids[0]);
//        List<Comment> List = dynamicService.getComments(ids[0]);
//        List<Comment> list = List;
//        for (int i = 1; i <ids.length-1 ; i++) {
//            commentId.append("#").append(ids[i]);
//            Comment entity = null;
//            for (int j = 0; j < list.size(); j++) {
//                if(list.get(j).getId().equals(commentId.toString())){
//                    entity = list.get(j);
//                    break;
//                }
//            }
//            if(entity.getComments()==null){
//                entity.setComments(new ArrayList<Comment>());
//            }
//            list = entity.getComments();
//        }
//        for (int i = 0; i < list.size(); i++) {
//            if(list.get(i).getId().equals(id)){
//                list.remove(i);
//            }
//        }
//        dynamicDao.updateComment(ids[0],List); // 刷新评论区
//        return List;
//    }

    /**
     * 添加评论
     *
     * @param dynamicId 动态id
     * @param comment   要添加的评论
     */
    @Override
    public boolean addComment(String dynamicId, Comment comment) {
         Dynamic dynamic = dynamicDao.getDynamicById(dynamicId);


        return false;
    }
}

package com.campus.contact.dao;

import com.alibaba.fastjson.JSONObject;
import com.campus.contact.domain.Comment;
import com.campus.contact.domain.Dynamic;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.*;


@Repository
public class DynamicDao {
    @Resource
    private MongoTemplate mongoTemplate;


    /**
     * 插入Dynamic
     */
    public String insert(Dynamic entity) {
        return mongoTemplate.save(entity, "dynamic").get_id();
    }

    /**
     * 更新Dynamic
     */
    public long update(String _id, Map<String, Object> change) {
        Query query = Query.query(Criteria.where("_id").is(_id));
        Update update = new Update();
        change.forEach(update::set);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "dynamic");
        return result.getModifiedCount();
    }

    /**
     * 往集合中的_id对象下的listName列表中插入新的元素object
     */
    public long insertSubList(String _id, String listName, Object object) {
        Query query = Query.query(Criteria.where("_id").is(_id));
        Update update = new Update();
        update.push(listName).each(object);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "dynamic");
        return result.getModifiedCount();
    }

    /**
     * 往集合中的dynamicId对象下的listName列表中(逻辑)删除元素_id
     */
    public long deleteSubList(String dynamicId, String listName, Object object) {
        Query query = Query.query(Criteria.where("_id").is(dynamicId));
        Update update = new Update();
        update.pull(listName, object);
        UpdateResult result = mongoTemplate.updateFirst(query, update, "dynamic");
        return result.getModifiedCount();
    }

    /**
     * 查找Dynamic
     *
     * @param criteria 筛选条件
     */
    public List<Dynamic> search(Criteria criteria) {
        criteria.and("deleted").is(false);
        Query query = new Query(criteria);
        query.with(Sort.by(
                Sort.Order.desc("updateTime")
        ));
        List<Dynamic> list = mongoTemplate.find(query, Dynamic.class, "dynamic");
        //排序
        for (Dynamic dynamic : list) {
            Collections.sort(dynamic.getComments());
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 根据id查找Dynamic
     * */
    public Dynamic getDynamicById(String dynamicId){
        return mongoTemplate.findById(dynamicId,Dynamic.class,"dynamic");
    }

    /**
     * 查看详情
     */
    public Dynamic detail(String dynamicId) {
        Dynamic dynamic = mongoTemplate.findById(dynamicId, Dynamic.class);
        List<Comment> list = getComments(dynamicId);
        dynamic.setComments(list);
        return dynamic;
    }

    /**
     * 获取动态下的所有评论
     */
    public List<Comment> getComments(String dynamicId) {
        Query query = new Query();
        FindIterable iterable = mongoTemplate.getCollection("dynamic").find(query.getQueryObject());
        Iterator iterator = iterable.iterator();
        while (iterator.hasNext()) {
            Document document = (Document) iterator.next();
            if (String.valueOf(document.get("_id")).equals(dynamicId)) {
                List list = document.get("comments", List.class);
                List<Comment> ret = new ArrayList<>();
                if (list == null)
                    return ret;
                for (int i = 0; i < list.size(); i++) {
                    String jsonStr = ((Document) list.get(i)).toJson();
                    Comment comment = JSONObject.parseObject(jsonStr, Comment.class);
                    ret.add(comment);
                }
                return ret;
            }
        }
        return null;
    }

    /**
     * 更新评论
     * */
    public boolean updateComment(String _id, List<Comment> list) {
        Query query = Query.query(Criteria.where("_id").is(_id));
        Update update = new Update();
        update.set("comments", list);
        mongoTemplate.updateFirst(query, update, "dynamic");
        return true;
    }

}

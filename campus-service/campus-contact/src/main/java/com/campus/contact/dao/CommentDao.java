package com.campus.contact.dao;

import com.campus.contact.domain.Comment;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Repository
public class CommentDao {
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     *插入comment
     * */
    public String insert(Comment entity){
        return mongoTemplate.save(entity,"comment").getUuid();
    }

    /**
     *更新comment
     * */
    public long update(String _id, Map<String,Object> change){
        Query query = Query.query(Criteria.where("_id").is(_id)); // 唯一
        Update update = new Update();
        change.forEach(update::set);
        UpdateResult result = mongoTemplate.updateFirst(query,update,"tb_comment");
        return result.getModifiedCount();
    }

    /**
     *查找comment
     * @param criteria 筛选条件
     * */
    public List<Comment> search(Criteria criteria){
        criteria.and("deleted").is(false);
        Query query = new Query(criteria);
        query.with(Sort.by(
                Sort.Order.desc("createTime")
        ));
        List<Comment> ret = mongoTemplate.findAll(Comment.class,"tb_comment");
        return ret;
    }
}

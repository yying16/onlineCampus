package com.campus.message.service;

import com.campus.message.domain.Message;
import com.campus.message.domain.Relationship;

public interface RelationshipService {

    /**
     * 插入关系
     * */
    String insert(Relationship relationship);
}

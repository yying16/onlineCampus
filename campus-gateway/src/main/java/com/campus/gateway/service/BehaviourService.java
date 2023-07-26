package com.campus.gateway.service;

import com.campus.gateway.dao.BehaviourDao;
import com.campus.gateway.domain.Behaviour;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class BehaviourService {

    @Autowired
    BehaviourDao behaviourDao;

    @Async
    public void asyncInsert(Behaviour behaviour) {
        behaviourDao.insert(behaviour);
    }
}
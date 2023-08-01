package com.campus.contact.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.contact.dao.BottleDao;
import com.campus.contact.domain.Bottle;
import com.campus.contact.service.BottleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class BottleServiceImpl implements BottleService {

    @Autowired
    BottleDao bottleDao;

    /**
     * 捞个漂流瓶(后面再拓展)
     */
    @Override
    public Bottle grabBottle(Integer category) {
       try{
           QueryWrapper<Bottle> wrapper = new QueryWrapper();
           wrapper.eq("deleted",0);
           wrapper.eq("category",category);
           wrapper.eq("status",0);
           List<Bottle> bottles = bottleDao.selectList(wrapper);
           if(bottles!=null&&bottles.size()>0){
               return bottles.get(0);
           }
       }catch (Exception e){
           log.info("获取漂流瓶失败");
           e.printStackTrace();
       }
        return null;
    }
}

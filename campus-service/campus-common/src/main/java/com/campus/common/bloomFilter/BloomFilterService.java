package com.campus.common.bloomFilter;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.campus.common.util.R;
import org.redisson.api.RBloomFilter;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: chb
 * @Date: 2023/08/24/14:30
 * @Description:
 */
@Component
public class BloomFilterService {

//    @Resource
//    private RBloomFilter<Object> bloomFilter;
//
//
//    public void bloomSet(String key){
//        boolean flag = bloomFilter.add(key);
//        if (flag != true){
//            LoggerFactory.getLogger(BloomFilterService.class).error("布隆过滤器插入值失败！");
//        }
//    }
//
//    public boolean bloomContain(String key){
//        boolean flag = bloomFilter.contains(key);
//        return flag == true ? true : false;
//    }
}

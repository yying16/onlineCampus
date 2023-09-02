package com.campus.common.config;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Author: chb
 * @Date: 2023/08/24/14:10
 * @Description:
 */
@Configuration
public class BloomFilterConfig {

    @Resource
    private RedissonClient redissonClient;

    @Bean(name = "RBloomFilter")
    public RBloomFilter<Object> bloom(){
        RBloomFilter<Object> bloomFilter = redissonClient.getBloomFilter("camps-filter");
        //初始化，容器100000.容错率千分之一
        bloomFilter.tryInit(100000,0.01);
        return bloomFilter;
    }

//    public static void main(String[] args) {
//        RBloomFilter<Object> bloomFilter = new BloomFilter().bloom();
//        bloomFilter.tryInit(10000,0.001);
//        //添加10000个
//        for (int i = 0; i < 100; i++) {
//            bloomFilter.add("YuShiwen" + i);
//        }
//        //用来统计误判的个数
//        int count = 0;
//        //查询不存在的数据一千次
//        for (int i = 0; i < 5; i++) {
//            if (bloomFilter.contains("xiaocheng" + i)) {
//                count++;
//            }
//        }
//        System.out.println("判断错误的个数："+count);
//        System.out.println("YuShiwen9999是否在过滤器中存在："+bloomFilter.contains("YuShiwen9999"));
//        System.out.println("YuShiwen11111是否在过滤器中存在："+bloomFilter.contains("YuShiwen11111"));
//        System.out.println("预计插入数量：" + bloomFilter.getExpectedInsertions());
//        System.out.println("容错率：" + bloomFilter.getFalseProbability());
//        System.out.println("hash函数的个数：" + bloomFilter.getHashIterations());
//        System.out.println("插入对象的个数：" + bloomFilter.count());
//    }
}

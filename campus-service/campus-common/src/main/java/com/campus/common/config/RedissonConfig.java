package com.campus.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class RedissonConfig {
    @Autowired
    private RedisProperties redisProperties;

    @Bean(name = "redissonClient")
    public RedissonClient redissonClient() {
        // 集群模式
//        Config config = new Config();
////        List<String> notes = redisProperties.getCluster().getNodes();
//        String master = "redis://117.72.15.203:6379";
//        Set<String> slaves = new HashSet() {{
//            add("redis://117.72.14.204:6379");
//            add("redis://117.72.13.205:6379");
//        }};
//        config.useMasterSlaveServers()
//                .setDatabase(redisProperties.getDatabase())
//                .setPassword("olcampus8888!")
//                .setMasterAddress(master)
//                .setSlaveAddresses(slaves); // 设置集群节点扫描间隔，单位毫秒，可选设置
//        return Redisson.create(config);

        // 单机模式
//        Config config = new Config();
        //指定编码，默认编码为org.redisson.codec.JsonJacksonCodec
        //config.setCodec(new org.redisson.client.codec.StringCodec());
//        config.useSingleServer()
//                .setAddress("redis://117.72.15.203:6379")
//                .setPassword("olcampus8888!")
//                .setConnectionPoolSize(50)
//                .setIdleConnectionTimeout(10000)
//                .setConnectTimeout(3000)
//                .setTimeout(3000)
//                .setDatabase(5);
//
//        return Redisson.create(config);

        // 哨兵模式
        Config config = new Config();
        config.useSentinelServers()
                .setMasterName("mymaster")
                .setPassword("olcampus8888!")
//                .setCheckSentinelsList(false)
                .addSentinelAddress("redis://117.72.15.203:26379", "redis://117.72.14.204:26379", "redis://117.72.13.205:26379")
                .setDatabase(0);
        RedissonClient redisson = Redisson.create(config);
        return redisson;

    }

}

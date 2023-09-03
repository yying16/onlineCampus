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
        Config config = new Config();
//        List<String> notes = redisProperties.getCluster().getNodes();
        String master = "redis://117.72.15.203:6379";
        Set<String> slaves = new HashSet() {{
            add("redis://117.72.14.204:6379");
//            add("redis://117.72.13.205:6379");
        }};
        config.useMasterSlaveServers()
                .setDatabase(redisProperties.getDatabase())
                .setPassword("olcampus8888!")
                .setMasterAddress(master)
                .setSlaveAddresses(slaves); // 设置集群节点扫描间隔，单位毫秒，可选设置
        return Redisson.create(config);
    }

}
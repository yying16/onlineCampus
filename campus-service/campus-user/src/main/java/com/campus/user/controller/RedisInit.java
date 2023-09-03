//package com.campus.user.controller;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.concurrent.TimeUnit;
//
///**
// * @author ygr
// * @date 2022-02-15 16:32
// */
//测验redis读写分离，打开注释，启动时会自动往redis写入数据，然后读取
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class RedisInit implements ApplicationRunner {
//
//    private final StringRedisTemplate redisTemplate;
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        for (int i = 0; i < 30; i++) {
//            try {
//                redisTemplate.opsForValue().set("k" + i, "v" + i);
//                log.info("set value success: {}", i);
//
//                Object val = redisTemplate.opsForValue().get("k" + i);
//                log.info("get value success: {}", val);
//                TimeUnit.SECONDS.sleep(1);
//            } catch (Exception e) {
//                log.error("error: {}", e.getMessage());
//            }
//        }
//        log.info("finished...");
//    }
//}

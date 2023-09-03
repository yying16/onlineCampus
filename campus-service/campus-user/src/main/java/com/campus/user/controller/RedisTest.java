package com.campus.user.controller;

import com.campus.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import kotlin.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RequestMapping("/test/redis")
@RestController
@Api(tags = "redis测试")
@Slf4j
public class RedisTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostMapping("/save")
    @ApiOperation("保存")
    public R save(@ApiParam("key") @RequestParam("key") String key, @ApiParam("value") @RequestParam("value") String value) {
        log.info("key-> {}, value -> {}", key, value);
        redisTemplate.opsForValue().set(key, value, 30, TimeUnit.SECONDS);
        return R.ok();
    }

    @GetMapping("/get")
    @ApiOperation("取值")
    public R get(@RequestParam("key") String key) {
        log.info("取值：key -> {}", key);
        Object o = redisTemplate.opsForValue().get(key);
        log.info("redisTemplate取值：value -> {}", o);
        return R.ok(o, "取值成功");
    }
}

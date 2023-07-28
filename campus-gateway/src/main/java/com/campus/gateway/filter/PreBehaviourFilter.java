package com.campus.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.util.TimeUtil;
import com.campus.gateway.domain.Behaviour;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 前置行为过滤器
 */
@Component
@Slf4j
@Order(2)
public class PreBehaviourFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("pre {}", System.currentTimeMillis());
        Behaviour behaviour = new Behaviour();
        ServerHttpRequest request = exchange.getRequest();
        behaviour.setBehaviourId(IdWorker.getIdStr(behaviour));
        behaviour.setUri(request.getURI().toString());
        behaviour.setHeader(JSONObject.toJSONString(request.getHeaders()));
        behaviour.setMethod(request.getMethodValue());
        behaviour.setUid(request.getHeaders().getFirst("uid"));
        behaviour.setParams(JSONObject.toJSONString(request.getQueryParams()));
        behaviour.setPath(request.getURI().getPath());
        behaviour.setService(getServiceName(behaviour.getPath()));
        behaviour.setRequestTime(TimeUtil.getCurrentTime());
        redisTemplate.opsForValue().set("behaviour" + behaviour.getBehaviourId(), JSONObject.toJSONString(behaviour)); // 将行为存放在redis中
        //修改请求头
        HttpHeaders customHeaders = new HttpHeaders();
        customHeaders.add("bid", behaviour.getBehaviourId());
        ServerHttpRequest requestWithCustomHeaders = request.mutate()
                .headers(httpHeaders -> httpHeaders.addAll(customHeaders))
                .build();
        ServerWebExchange exchangeWithCustomHeaders = exchange.mutate().request(requestWithCustomHeaders).build();
        return chain.filter(exchangeWithCustomHeaders);
    }

    /**
     * 根据uri获取服务名
     */
    private String getServiceName(String uri) {
        try {
            String input = uri;
            String regex = "campus[A-Z|a-z]+"; // 匹配电话号码的正则表达式
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(input);
            matcher.find();
            String service = matcher.group(); // 获取匹配的子串
            return service;
        } catch (Exception e) {
            return "UnKnown";
        }
    }
}

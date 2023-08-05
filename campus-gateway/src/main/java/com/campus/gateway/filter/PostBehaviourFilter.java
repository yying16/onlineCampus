package com.campus.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.campus.gateway.domain.Behaviour;
import com.campus.gateway.service.BehaviourService;
import com.campus.gateway.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 后置行为过滤器
 */
@Component
@Slf4j
@Order(1)
public class PostBehaviourFilter implements GlobalFilter {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    BehaviourService behaviourService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("post {}", System.currentTimeMillis());
            String bid = exchange.getRequest().getHeaders().getFirst("bid");
            String behaviourStr = redisTemplate.opsForValue().get("behaviour"+bid);
            Behaviour behaviour = JSONObject.parseObject(behaviourStr, Behaviour.class);
            ServerHttpResponse response = exchange.getResponse();
            behaviour.setStatus(response.getRawStatusCode());
            behaviour.setReasonPhrase(Objects.requireNonNull(response.getStatusCode()).getReasonPhrase());
            behaviour.setResponseTime(TimeUtil.getCurrentTime());
            behaviour.setDuration(TimeUtil.getTimeInterval(behaviour.getRequestTime(),behaviour.getResponseTime()));
            behaviourService.asyncInsert(behaviour);
            redisTemplate.delete("behaviour"+bid);
        }));
    }
}

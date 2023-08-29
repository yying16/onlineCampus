package com.campus.gateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.campus.gateway.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * @Author: chb
 * @Date: 2023/08/25/10:29
 * @Description:
 */
@Order(1)
@Component
public class AuthFilter implements GlobalFilter {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getHeaders().getFirst("token");
        assert token != null;
        if(token.equals("t1")||token.equals("t2")||token.equals("t3")||token.equals("t4")){ // 测试账号直接通过
            return chain.filter(exchange);
        }
        String uid = TokenUtil.getUidFromToken(token);
        String userJson = stringRedisTemplate.opsForValue().get("user"+uid);
        JSONObject jsonObject = JSONObject.parseObject(userJson);
        Integer auth = (Integer) jsonObject.get("auth");
        if (auth == null || auth.equals(0)){
            exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
            return exchange.getResponse().setComplete();
        }

        return chain.filter(exchange);
    }

}

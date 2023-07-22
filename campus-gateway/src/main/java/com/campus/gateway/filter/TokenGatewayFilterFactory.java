package com.campus.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;

import java.util.Arrays;
import java.util.List;

/**
 * token filter
 */
@Component
@Slf4j
@ConfigurationProperties(prefix = "exclude")
public class TokenGatewayFilterFactory extends AbstractGatewayFilterFactory<TokenGatewayFilterFactory.Config> {

    @Autowired
    StringRedisTemplate redisTemplate;

    private String[] patterns;

    public void setPatterns(String[] patterns) {
        this.patterns = patterns;
    }

    public TokenGatewayFilterFactory() {
        super(Config.class);
        log.info("Loaded GatewayFilterFactory [Token]");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("enabled");
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            try {
                if (!config.isEnabled()) { // 未开启过滤
                    log.info("Token Filter is unable!");
                    return chain.filter(exchange);
                }
                ServerHttpRequest request = exchange.getRequest();
                HttpHeaders headers = request.getHeaders();
                String path = request.getURI().getPath();
                log.info("path = {}", path);
                for (String p : patterns) { // patterns中的路径无须校验token
                    if (path.contains(p)) {
                        return chain.filter(exchange);
                    }
                }
                String token = headers.getFirst("token");
                String uid = headers.getFirst("uid"); // uid用于未携带token时进行缓存区获取token
                if (token == null) {
                    token = request.getQueryParams().getFirst("token");
                }
                if (uid == null) {
                    uid = request.getQueryParams().getFirst("uid");
                }
                log.info("token is {} and uid is {}", token, uid);
                ServerHttpResponse response = exchange.getResponse();
                if (token == null || token.length() == 0 || uid == null || uid.length() == 0) { // 令牌过期
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return response.setComplete();
                }
                String authToken = redisTemplate.opsForValue().get(uid); // 获取用户的token
                log.info("authToken is {}", authToken);
                if (authToken == null || !authToken.equals(token)) {
                    response.setStatusCode(HttpStatus.UNAUTHORIZED); // 返回401错误
                    return response.setComplete();
                }
                // 1. 获取ServerHttpRequest对象
                ServerHttpRequest originalRequest = exchange.getRequest();
                // 2. 创建自定义请求头
                HttpHeaders customHeaders = new HttpHeaders();
                customHeaders.add("test", "ttttttttt");
                // 3. 创建新的ServerHttpRequest对象并添加自定义请求头
                ServerHttpRequest requestWithCustomHeaders = originalRequest.mutate()
                        .headers(httpHeaders -> httpHeaders.addAll(customHeaders))
                        .build();
                // 4. 创建新的ServerWebExchange对象并替换原始的请求
                ServerWebExchange exchangeWithCustomHeaders = exchange.mutate().request(requestWithCustomHeaders).build();
                return chain.filter(exchangeWithCustomHeaders);
            } catch (RedisConnectionFailureException e) {
                e.printStackTrace();
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED); // 返回401错误
                return response.setComplete();
            }
        };
    }

    public static class Config {
        // 控制是否开启认证
        private boolean enabled;

        public Config() {
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

}

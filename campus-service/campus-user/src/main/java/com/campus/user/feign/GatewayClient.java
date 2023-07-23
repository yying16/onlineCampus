package com.campus.user.feign;

import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "campus-gateway", fallbackFactory = GatewayFeignFactory.class)
@Component
public interface GatewayClient {
    @PostMapping("/generalToken")
    String generalToken(@RequestBody JSONObject user);
}

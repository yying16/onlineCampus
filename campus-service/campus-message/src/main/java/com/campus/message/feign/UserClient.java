package com.campus.message.feign;

import com.campus.common.util.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "campus-user", fallbackFactory = UserFeignFactory.class)
@Component
public interface UserClient {

    @GetMapping("/campus-user/user/getAllUserId")
    R getAllUserId();

    @GetMapping("/getAutoReply/{userId}")
    public R getAutoReply(@PathVariable String userId);
}

package com.campus.trade.feign;

import com.campus.common.util.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @auther xiaolin
 * @create 2023/7/13 11:25
 */
@FeignClient(value = "campus-user",fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/campusUser/user/{userId}")
    public R getUserById(@ApiParam("用户id") @PathVariable("userId") String userId);
}

package com.campus.parttime.feign;


import com.campus.common.util.R;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value = "campus-user", fallbackFactory = UserFeignFactory.class)
public interface UserClient {
    /**
     * 根据用户Id获取用户信息
     */
    @GetMapping("/campusUser/user/{userId}")
    public R getUserById(@ApiParam("用户id") @PathVariable("userId") String userId);
}

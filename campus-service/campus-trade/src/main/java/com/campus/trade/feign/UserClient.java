package com.campus.trade.feign;

import com.campus.common.util.R;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @auther xiaolin
 * @create 2023/7/13 11:25
 */
@FeignClient(value = "campus-user",fallbackFactory = UserClientFallbackFactory.class)
public interface UserClient {

    @GetMapping("/campusUser/user/{userId}")
    public R getUserById(@ApiParam("用户id") @PathVariable("userId") String userId);

    @GetMapping("/campusUser/user/getBalance/{userId}")
    public BigDecimal getBalance(@ApiParam("用户id") @PathVariable("userId")  String userId);

    @PutMapping("/campusUser/user/updateBalance/{userId}/{balance}")
    public R updateBalance(@ApiParam("用户id") @PathVariable("userId")  String userId, @PathVariable("balance") BigDecimal balance);

    @PostMapping("/campusUser/user/detailsChange/addDetailsChange")
    public R addDetailsChange(Map<String, Object> map);
}

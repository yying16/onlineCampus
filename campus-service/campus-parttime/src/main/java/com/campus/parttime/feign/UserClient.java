package com.campus.parttime.feign;


import com.campus.common.util.R;
import com.campus.message.dto.HandleRequestForm;
import com.campus.message.dto.MessageForm;
import com.campus.message.dto.PromptInformationForm;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@Component
@FeignClient(value = "campus-user", fallbackFactory = UserFeignFactory.class)
public interface UserClient {
    /**
     * 根据用户Id获取用户信息
     */
    @GetMapping("/campusUser/user/getUserById")
    public R getUserById(@RequestParam("userId") String userId);
}

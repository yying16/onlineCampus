package com.campus.user.feign;

import com.campus.common.util.R;
import com.campus.user.pojo.PromptInformationForm;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "campus-message", fallbackFactory = MessageFeignFactory.class)
@Component
public interface MessageClient {

    /**
     * 发送系统消息
     */
    @PostMapping("/campusMessage/message/sendPromptInformation")
   R sendPromptInformation(@RequestBody PromptInformationForm form);

    /**
     * 登录时用户初始化信息
     */
    @GetMapping("/campusMessage/message/initMessage")
    R initMessage(@RequestHeader("uid") String uid);

    /**
     * 退出登录时清除用户消息缓存区
     */
    @GetMapping("/campusMessage/message/clearCache")
    R clearCache(@RequestHeader("uid") String uid);

    /**
     * 用户是否登录
     * @param uid
     * @return
     */
    @GetMapping("/campusMessage/isOnline")
    R isOnline(@RequestHeader("uid") String uid);

}

package com.campus.parttime.feign;


import com.campus.common.util.R;
import com.campus.parttime.pojo.HandleRequestForm;
import com.campus.parttime.pojo.MessageForm;
import com.campus.parttime.pojo.PromptInformationForm;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Component
@FeignClient(value = "campus-message", fallbackFactory = MessageFeignFactory.class)

public interface MessageClient {
    /**
    * 发送消息(系统/用户/请求)
    */
    @PostMapping("/campusMessage/message/send")
    public R send(@RequestBody MessageForm form);

    /**
     * 用户处理好友请求
     */
    @PostMapping("/campusMessage/message/handleRequest")
    public R handleRequest(@RequestBody HandleRequestForm form, @RequestHeader("uid") String uid);

    /**
     * 发送系统提示信息
     */
    @PostMapping("/campusMessage/message/sendPromptInformation")
    public R sendPromptInformation(@RequestBody PromptInformationForm form);
}

package com.campus.message.controller;

import com.campus.common.util.R;
import com.campus.message.domain.Message;
import com.campus.message.feign.UserClient;
import com.campus.message.service.impl.MessageServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/message")
@Api("消息通讯接口")
public class MessageController {

    @Autowired
    MessageServiceImpl messageService;

    @Autowired
    UserClient userClient;

    @ApiOperation("发送消息(系统/用户/请求)")
    @PostMapping("/send")
    public R send(@RequestBody Message message){
        if (messageService.sendMessage(message)) {
            return R.ok();
        }else {
            return R.failed();
        }
    }

    @ApiOperation("用户登录后的消息初始化")
    @PostMapping("/initMessage")
    public R initMessage(){
        return R.failed();
    }

    @ApiOperation("用户查看消息懒加载")
    @PostMapping("/lazyLoading")
    public R lazyLoading(){
        return R.failed();
    }

}

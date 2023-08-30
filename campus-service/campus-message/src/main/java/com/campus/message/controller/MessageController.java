package com.campus.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.message.domain.Message;
import com.campus.message.dto.HandleRequestForm;
import com.campus.message.dto.MessageForm;
import com.campus.message.dto.PromptInformationForm;
import com.campus.message.service.UserOnlineService;
import com.campus.message.service.impl.MessageServiceImpl;
import com.campus.message.service.impl.UserOnlineServiceImpl;
import com.campus.message.vo.InitUserMessageData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@Api("消息通讯接口")
public class MessageController {

    @Autowired
    MessageServiceImpl messageService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserOnlineServiceImpl userOnlineService;


//    @ApiOperation("心跳检测")
//    @GetMapping("/heartTest/{uid}")
//    public R heartTest(@PathVariable String uid) {
//        return R.ok();
//        String flag = redisTemplate.opsForValue().get("heart_" + uid);
//        if (flag == null || flag.length() == 0) {
//            return R.failed();
//        }
//        if (Boolean.parseBoolean(flag)) {
//            return R.ok();
//        }
//        return R.failed();
//    }


    @ApiOperation("发送消息(系统/用户/请求)")
    @PostMapping("/send")
    public R send(@RequestBody MessageForm form) {
        Message message = FormTemplate.analyzeTemplate(form, Message.class);
        if (messageService.sendMessage(message)) {
            return R.ok(null, "发送成功");
        } else {
            return R.failed(null, "发送失败");
        }
    }

    @ApiOperation("用户登录后的消息初始化")
    @GetMapping("/initMessage")
    public R initMessage(@RequestHeader("uid") String uid) {
        JSONObject jsonObject = messageService.initMessage(uid);
        messageService.clickMyMessage(uid); // 添加会话缓存数据
        if (jsonObject != null) {
            return R.ok(jsonObject);
        }
        return R.failed();
    }

    @ApiOperation("用户点击【首页】->【消息】时返回的好友消息会话列表")
    @GetMapping("/clickMyMessage")
    public R clickMyMessage(@RequestHeader("uid") String uid){
        List<InitUserMessageData> list = messageService.clickMyMessage(uid);
        if (list != null) {
            return R.ok(list);
        }
        return R.failed();
    }


    @ApiOperation("用户消息初始化")
    @GetMapping("/initUserMessage")
    public R initUserMessage(@RequestHeader("uid") String uid) {
        List<InitUserMessageData> list = messageService.initUserMessage(uid);
        if (list != null) {
            return R.ok(list);
        }
        return R.failed();
    }


    @ApiOperation("用户处理好友请求")
    @PostMapping("/handleRequest")
    public R handleRequest(@RequestBody HandleRequestForm form, @RequestHeader("uid") String uid) {
        if (messageService.handleRequest(uid, form.getMsgId(), form.getAccept())) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("发送系统提示信息")
    @PostMapping("/sendPromptInformation")
    public R sendPromptInformation(@RequestBody PromptInformationForm form) {
        if (messageService.sendPromptInformation(form.getReceiver(), form.getContent())) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("获取用户与好友的聊天记录(用户点击对应聊天窗口)")
    @GetMapping("/getUserChatRecords")
    public R getUserChatRecords(@RequestParam("friendId") String friendId, @RequestHeader("uid") String uid) {
        JSONObject jsonObject = messageService.getUserChatRecords(uid, friendId);
        if (jsonObject == null) {
            return R.failed();
        }
        return R.ok(jsonObject);
    }

    @ApiOperation("懒加载用户与好友的聊天记录")
    @GetMapping("/lazyLoadingChatRecords")
    public R lazyLoadingChatRecords(@RequestParam("friendId") String friendId, @RequestHeader("uid") String uid) {
        if (messageService.lazyLoadingChatRecords(uid, friendId)) {
            JSONObject jsonObject = messageService.getUserChatRecords(uid, friendId);
            if (jsonObject == null) {
                return R.failed();
            }
            return R.ok(jsonObject);
        }
        return R.failed("数据加载失败");
    }

    @ApiOperation("用户退出登录进行缓存删除")
    @GetMapping("/clearCache")
    public R clearCache(@RequestHeader("uid") String uid) {
        if (messageService.clearCache(uid)) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("清除未读")
    @GetMapping("clearUnRead")
    public R clearUnRead(@RequestHeader("uid") String uid) {
        if (messageService.clearUnRead(uid)) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("用户是否登录")
    @GetMapping("/isOnline")
    public R isOnline(@RequestHeader("uid")String uid){
        if(userOnlineService.isOnline(uid)){
            return R.ok(true);
        }
        return R.failed(false);
    }


}

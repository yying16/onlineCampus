package com.campus.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.common.util.R;
import com.campus.common.util.SpringContextUtil;
import com.campus.message.dao.MessageDao;
import com.campus.message.domain.Message;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/relationship")
@Api("关系相关接口")
public class RelationshipController {

    @Autowired
    MessageDao messageDao;

    @ApiOperation("发起好友请求")
    @PostMapping("/initiateFriend")
    public R initiateFriend(@RequestHeader("uid") String uid, @RequestBody Message message) {
        return R.failed();
    }

    @ApiOperation("接收好友请求")
    @GetMapping("/acceptRequest")
    public R acceptRequest(@RequestHeader("uid") String uid, @Param("msgId") String msgId) {
        return R.failed();
    }

    @GetMapping("/test")
    public R test(){
         Map userDetail = messageDao.getUserDetail("1");
        String json = JSONObject.toJSONString(userDetail);
        return R.ok(json);
    }


}

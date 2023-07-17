package com.campus.message.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.common.util.R;
import com.campus.common.util.SpringContextUtil;
import com.campus.message.dao.MessageDao;
import com.campus.message.domain.Message;
import com.campus.message.pojo.User;
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

}

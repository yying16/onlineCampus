package com.campus.message.controller;

import com.campus.common.util.R;
import com.campus.message.service.impl.MatchServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api("匹配交友")
@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    MatchServiceImpl matchService;

    @ApiOperation("开始好友匹配")
    @GetMapping("/match")
    public R match(@RequestHeader("uid")String uid){
        if(matchService.match(uid)){
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("取消好友匹配")
    @GetMapping("/cacheMatch")
    public R cacheMatch(@RequestHeader("uid")String uid){
        if(matchService.cacheMatch(uid)){
            return R.ok();
        }
        return R.failed();
    }

}

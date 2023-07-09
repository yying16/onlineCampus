package com.campus.user.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.user.domain.User;
import com.campus.user.dto.UpdatePasswordForm;
import com.campus.user.service.impl.UserServiceImpl;
import com.campus.user.util.TokenUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/getDetail")
    public R getDetail(@RequestHeader("token") String token) {
        User user = TokenUtil.getClaimsFromToken(token);
        if (user == null) {
            return R.failed("令牌无效");
        }
        if (Objects.equals(redisTemplate.opsForValue().get(user.getUserId()), token)) {
            return R.ok(user);
        } else {
            return R.failed("令牌无效");
        }

    }


    /**
     * 获取系统中所有普通用户id
     */
    @GetMapping("/getAllUserId")
    public R getAllUserId() {
        List<String> allUserId = userService.getAllUserId();
        if (allUserId == null || allUserId.size() == 0) {
            return R.failed();
        }
        return R.ok(allUserId);
    }

    /**
     * 获取用户的自动回复内容
     */
    @GetMapping("/getAutoReply/{userId}")
    public R getAutoReply(@PathVariable String userId) {
        String ret = userService.getAutoReply(userId);
        if(ret==null){
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword/{userId}")
    public R updatePassword(@PathVariable(value = "userId") String userId, @RequestBody UpdatePasswordForm form) {
        boolean b = userService.updatePassword(userId, form);
        if (b) {
            return R.ok();
        }
        return R.failed();
    }

}

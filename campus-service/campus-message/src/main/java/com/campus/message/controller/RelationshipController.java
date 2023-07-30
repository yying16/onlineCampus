package com.campus.message.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.message.domain.Relationship;
import com.campus.message.pojo.User;
import com.campus.message.service.impl.RelationshipServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.campus.message.constant.RelationshipStatus.NORMAL;

@RestController
@RequestMapping("/relationship")
@Api("关系相关接口")
public class RelationshipController {

    @Autowired
    RelationshipServiceImpl relationshipService;

    @Autowired
    ServiceCenter serviceCenter;

    @ApiOperation("查看我的好友列表")
    @GetMapping("/getFriendList")
    public R getFriendList(@RequestHeader("uid") String uid) {
        List<User> friends = relationshipService.getFriends(uid);
        if (friends != null) {
            return R.ok(friends);
        }
        return R.failed("获取好友列表失败");
    }

    @ApiOperation("删除好友")
    @GetMapping("/deleteFriend")
    public R deleteFriend(@RequestHeader("uid") String uid, @RequestParam("friendId") String friendId) {
        if (relationshipService.deleteFriend(uid, friendId)) {
            return R.ok(null, "删除好友成功");
        }
        return R.failed(null, "删除好友失败");
    }

    @ApiOperation("屏蔽好友")
    @GetMapping("/blockFriend")
    public R blockFriend(@RequestHeader("uid") String uid, @RequestParam("friendId") String friendId) {
        if (relationshipService.blockFriend(uid, friendId)) {
            return R.ok(null, "屏蔽好友成功");
        }
        return R.failed(null, "屏蔽好友失败");
    }

    @ApiOperation("判断对方是否为我的好友")
    @GetMapping("/isFriend")
    public R isFriend(@RequestHeader("uid") String uid, @RequestParam("friendId") String friendId) {
        List<User> friends = relationshipService.getFriends(uid);
        if (friends != null) {
            List<User> filter = friends.stream().filter(f -> f.getUserId().equals(friendId)).collect(Collectors.toList());
            if (filter.size() == 0) { // 好友列表中没有该用户
                return R.ok(false);
            }
            return R.ok(true);
        }
        return R.failed("获取好友列表失败");
    }

    @ApiOperation("添加好友开始聊天")
    @GetMapping("/addFriendToChat")
    public R addFriendToChat(@RequestHeader("uid") String uid, @RequestParam("friendId") String friendId) {
        List<User> friends = relationshipService.getFriends(uid);
        if (friends != null) {
            List<User> filter = friends.stream().filter(f -> f.getUserId().equals(friendId)).collect(Collectors.toList());
            if (filter.size() != 0) { // 好友列表已经有该用户
                return R.ok(true); // 已经是好友就直接打开聊天窗口
            } else {
                // 好友列表中没有该用户->两者还不是好友，就添加好友
                Relationship relationship = new Relationship();
                relationship.setSender(uid);
                relationship.setReceiver(friendId);
                relationship.setRelationshipId(IdWorker.getIdStr(relationship));
                relationship.setStatus(NORMAL.code);
                relationshipService.addFriend(relationship);
            }
        }
        return R.failed(null, "获取好友列表失败");
    }

}

package com.campus.contact.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.contact.domain.Bottle;
import com.campus.contact.domain.Reply;
import com.campus.contact.dto.AddBottleForm;
import com.campus.contact.dto.ReplyBottleForm;
import com.campus.contact.service.BottleService;
import com.campus.contact.service.impl.BottleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.campus.contact.constant.BottleStatus.NORMAL;

@RestController
@RequestMapping("/bottle")
@Api("漂流瓶")
public class BottleController {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    BottleServiceImpl bottleService;

    @ApiOperation("丢个漂流瓶")
    @PostMapping("/addBottle")
    public R addBottle(@RequestBody AddBottleForm form,@RequestHeader("uid")String uid) {
        Bottle bottle = FormTemplate.analyzeTemplate(form, Bottle.class);
        assert bottle != null;
        bottle.setBottleId(IdWorker.getIdStr(bottle));
        bottle.setStatus(NORMAL.code);
        bottle.setPromulgatorId(uid);
        if (serviceCenter.insertMySql(bottle)) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("捞个漂流瓶")
    @GetMapping("/grabBottle")
    public R grabBottle(@RequestParam("category") Integer category) {
        final Bottle bottle = bottleService.grabBottle(category);
        if (bottle != null) {
            // 装配回复内容
            Map map = new HashMap() {{
                put("bottleId", bottle.getBottleId());
                put("deleted", false);
            }};
            List search = serviceCenter.search(map, Reply.class);
            bottle.setReplies(search);
            serviceCenter.increment(bottle.getBottleId(),Bottle.class,true,"visits");
            return R.ok(bottle);
        }
        return R.failed(null, "获取漂流瓶失败");
    }

    @ApiOperation("查看漂流瓶详情")
    @GetMapping("/getBottleDetail")
    public R getBottleDetail(@RequestParam("bottleId") String bottleId) {
        Bottle bottle = (Bottle) serviceCenter.selectMySql(bottleId, Bottle.class);
        if (bottle != null) {
            // 装配回复内容
            Map map = new HashMap() {{
                put("bottleId", bottleId);
                put("deleted", false);
            }};
            List search = serviceCenter.search(map, Reply.class);
            bottle.setReplies(search);
            serviceCenter.increment(bottle.getBottleId(),Bottle.class,true,"visits");
            return R.ok(bottle);
        }
        return R.failed();
    }

    @ApiOperation("回复漂流瓶")
    @PostMapping("/replyBottle")
    public R replyBottle(@RequestBody ReplyBottleForm form,@RequestHeader("uid")String uid) {
        Reply reply = FormTemplate.analyzeTemplate(form, Reply.class);
        assert reply != null;
        reply.setReplyId(IdWorker.getIdStr(reply));
        reply.setPromulgatorId(uid);
        if (serviceCenter.insertMySql(reply)) { // 数据写入成功
            if (serviceCenter.increment(reply.getBottleId(),Bottle.class,false,"replyNum")) { // 更新漂流瓶回复数
                return R.ok();
            }
            return R.failed(null,"数据更新失败");
        }
        return R.failed(null,"数据插入失败");
    }

    @ApiOperation("查询我的漂流瓶")
    @GetMapping("/searchMyBottle")
    public R searchMyBottle(@RequestHeader("uid") String uid) {
        Map map = new HashMap() {{
            put("promulgatorId", uid);  // 发布者账号为uid
            put("deleted", false);// 未删除
        }};
        List search = serviceCenter.search(map, Bottle.class);
        if (search != null) {
            return R.ok(search);
        }
        return R.failed();
    }

    @ApiOperation("查看我的回复")
    @GetMapping("/searchMyReply")
    public R searchMyReply(@RequestHeader("uid") String uid) {
        Map map = new HashMap() {{
            put("promulgatorId", uid);  // 发布者账号为uid
            put("deleted", false); // 未删除
        }};
        List search = serviceCenter.search(map, Reply.class);
        if (search != null) {
            return R.ok(search);
        }
        return R.failed();
    }

    @ApiOperation("删除漂流瓶")
    @GetMapping("/deleteBottle")
    public R deleteBottle(@RequestParam("bottleId") String bottleId) {
        if (serviceCenter.deleteMySql(Bottle.class, bottleId)) {// 主页项直接调用套件存储
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("删除我的回复")
    @GetMapping("/deleteReply")
    public R deleteReply(@RequestParam("replyId") String replyId) {
        if (serviceCenter.deleteMySql(Reply.class, replyId)) {// 主页项直接调用套件存储
            return R.ok();
        }
        return R.failed();
    }

}

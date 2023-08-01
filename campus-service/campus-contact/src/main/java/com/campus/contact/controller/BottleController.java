package com.campus.contact.controller;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.contact.domain.Bottle;
import com.campus.contact.domain.Reply;
import com.campus.contact.dto.AddBottleForm;
import com.campus.contact.dto.ReplyBottleForm;
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

    @ApiOperation("丢个漂流瓶")
    @PostMapping("/addBottle")
    public R addBottle(@RequestBody AddBottleForm form) {
        Bottle bottle = FormTemplate.analyzeTemplate(form, Bottle.class);
        assert bottle != null;
        bottle.setStatus(NORMAL.code);
        if (serviceCenter.insertMySql(bottle)) {
            return R.ok();
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

    @ApiOperation("查看漂流瓶详情")
    @GetMapping("/getBottleDetail")
    public R getBottleDetail(@RequestParam("bottleId") String bottleId) {
        Bottle bottle = (Bottle) serviceCenter.selectMySql(bottleId, Bottle.class);
        if (bottle != null) {
            return R.ok(bottle);
        }
        return R.failed();
    }

    @ApiOperation("回复漂流瓶")
    @PostMapping("/replyBottle")
    public R replyBottle(@RequestBody ReplyBottleForm form){
        Reply reply = FormTemplate.analyzeTemplate(form, Reply.class);
        assert reply != null;
        if (serviceCenter.insertMySql(reply)) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("查询我的漂流瓶")
    @PostMapping("/searchMyBottle")
    public R searchMyBottle(@RequestHeader("uid") String uid) {
        Map map = new HashMap(){{
           put("promulgatorId",uid);  // 发布者账号为uid
        }};
        List search = serviceCenter.search(map, Bottle.class);
        if (search != null) {
            return R.ok(search);
        }
        return R.failed();
    }

//    @ApiOperation("捞个漂流瓶")
//    @PostMapping("/grabBottle")
//    public R grabBottle(){
//
//    }
}

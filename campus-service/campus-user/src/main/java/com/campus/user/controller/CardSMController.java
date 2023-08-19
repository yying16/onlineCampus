package com.campus.user.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.util.R;
import com.campus.user.domain.CardSM;
import com.campus.user.dto.AddCardSMForm;
import com.campus.user.service.CardSMService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:49
 */

@RestController
@RequestMapping("/cardsm")
@Log4j2
@Api(tags = "卡密类型相关接口")
public class CardSMController {


    @Autowired
    private CardSMService cardSMService;

    //添加卡密类型
    @ApiOperation(value = "添加卡密类型", notes = "添加卡密类型")
    @PostMapping("/addCardSM")
    public R addCardSM(@RequestBody AddCardSMForm addCardSMForm) {
        log.info("添加卡密类型");
        //有效期不能小于0
        if (addCardSMForm.getValidity()<0){
            return R.failed(null,"有效期不能小于0");
        }
        boolean b = cardSMService.addCardSM(addCardSMForm);
        if (!b) {
            return R.failed(null,"添加卡密类型失败");
        }
        return R.ok(null, "添加卡密类型成功");
    }

    //删除卡密类型
    @ApiOperation(value = "删除卡密类型", notes = "删除卡密类型")
    @DeleteMapping("/deleteCardSM/{cardsmId}")
    public R deleteCardSM(@PathVariable("cardsmId") String cardsmId) {
        log.info("删除卡密类型");
        boolean b = cardSMService.removeById(cardsmId);
        if (!b) {
            return R.failed(null,"删除卡密类型失败");
        }
        return R.ok(null, "删除卡密类型成功");
    }

    //修改卡密类型
    @ApiOperation(value = "修改卡密类型", notes = "修改卡密类型")
    @PutMapping("/updateCardSM/{cardsmId}")
    public R updateCardSM(@PathVariable("cardsmId") String cardsmId,@RequestBody AddCardSMForm addCardSMForm) {

        //根据id查询卡密类型
        CardSM cardSM = cardSMService.getById(cardsmId);
        if (cardSM == null) {
            return R.failed(null,"卡密类型不存在");
        }
        BeanUtils.copyProperties(addCardSMForm, cardSM);
        log.info("修改卡密类型");
        boolean b = cardSMService.updateById(cardSM);
        if (!b) {
            return R.failed(null,"修改卡密类型失败");
        }
        return R.ok(null, "修改卡密类型成功");
    }

    //查询卡密类型
    @ApiOperation(value = "查询卡密类型", notes = "查询卡密类型")
    @GetMapping("/getCardSM/{cardsmId}")
    public R getCardSM(@PathVariable("cardsmId") String cardsmId) {
        log.info("查询卡密类型");
        CardSM cardSM = cardSMService.getById(cardsmId);
        if (cardSM == null) {
            return R.failed(null,"卡密类型不存在");
        }
        return R.ok(cardSM, "查询卡密类型成功");
    }

    //分页查询卡密类型列表
    @ApiOperation(value = "分页查询卡密类型列表", notes = "分页查询卡密类型列表")
    @GetMapping("/getCardSMList/{page}/{size}")
    public R getCardSMList(@PathVariable("page") Integer page, @PathVariable("size") Integer size) {
        log.info("分页查询卡密类型列表");
        Page<CardSM> cardSMPage = new Page<>(page, size);
        cardSMService.page(cardSMPage);
        return R.ok(cardSMPage, "分页查询卡密类型列表成功");
    }



}

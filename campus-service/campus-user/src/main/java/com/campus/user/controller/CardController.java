package com.campus.user.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.common.util.R;
import com.campus.user.domain.Card;
import com.campus.user.dto.AddCardForm;
import com.campus.user.dto.QueryCardForm;
import com.campus.user.dto.UpdateCardForm;
import com.campus.user.service.CardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:31
 */

@RestController
@RequestMapping("/card")
@Log4j2
@Api(tags = "卡密相关接口")
public class CardController {

    @Autowired
    private CardService cardService;

    //批量生成卡密
    @ApiOperation(value = "批量生成卡密", notes = "批量生成卡密")
    @PostMapping("/addCard")
    public R addCard(AddCardForm addCardForm) {
        log.info("批量生成卡密");
        boolean b = cardService.addCard(addCardForm);
        if (!b) {
            return R.failed(null,"批量生成卡密失败");
        }
        return R.ok(null, "批量生成卡密成功");
    }

    //查询卡密
    @ApiOperation(value = "查询卡密", notes = "查询卡密")
    @GetMapping("{cardId}")
    public R queryCard(@PathVariable("cardId") String cardId) {
        log.info("查询卡密");
        Card b = cardService.getById(cardId);
        if (b == null) {
            return R.failed(null,"查询卡密失败");
        }
        return R.ok(b, "查询卡密成功");
    }

    //删除卡密
    @ApiOperation(value = "删除卡密", notes = "删除卡密")
    @DeleteMapping("{cardId}")
    public R deleteCard(@PathVariable("cardId") String cardId) {
        log.info("删除卡密");
        boolean b = cardService.removeById(cardId);
        if (!b) {
            return R.failed(null,"删除卡密失败");
        }
        return R.ok(null, "删除卡密成功");
    }

    //修改卡密
    @ApiOperation(value = "修改卡密", notes = "修改卡密")
    @PutMapping("{cardId}")
    public R updateCard(@PathVariable("cardId") String cardId, @RequestBody UpdateCardForm updateCardForm) {
        log.info("修改卡密");
        Card card = new Card();
        card.setId(cardId);
        BeanUtil.copyProperties(updateCardForm, card);
        boolean b = cardService.updateById(card);
        if (!b) {
            return R.failed(null,"修改卡密失败");
        }
        return R.ok(null, "修改卡密成功");
    }

    //分页按条件查询卡密列表
    @ApiOperation(value = "分页按条件查询卡密列表", notes = "分页按条件查询卡密列表")
    @GetMapping("/list/{page}/{size}")
    public R listCard(@PathVariable("page") Integer page, @PathVariable("size") Integer size, QueryCardForm card) {
        log.info("分页按条件查询卡密列表");
        Page<Card> cardPage = new Page<>(page, size);
        cardService.listCard(cardPage, card);
        return R.ok(cardPage, "分页按条件查询卡密列表成功");
    }

    //批量删除卡密
    @ApiOperation(value = "批量删除卡密", notes = "批量删除卡密")
    @DeleteMapping("/batchDelete")
    public R batchDeleteCard(@RequestBody List<String> cardIds) {
        log.info("批量删除卡密");
        boolean b = cardService.removeByIds(cardIds);
        if (!b) {
            return R.failed(null,"批量删除卡密失败");
        }
        return R.ok(null, "批量删除卡密成功");
    }

    //使用卡密
    @ApiOperation(value = "使用卡密", notes = "使用卡密")
    @PostMapping("/useCard/{cardKey}")
    public R useCard(@PathVariable("cardKey") String cardKey,@RequestHeader("uid") String uid) {
        log.info("使用卡密");
       return cardService.useCard(cardKey,uid);

    }




}

package com.campus.trade.controller;

import com.campus.common.service.ServiceCenter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auther xiaolin
 * @create 2023/7/9 14:30
 */
@RestController
@RequestMapping("/bid")
@Api(tags = "出价管理")
public class BidController {


    @Autowired
    ServiceCenter serviceCenter;


    //用户添加出价
    @PostMapping("/addBid/{productId}/{price}")
    @ApiOperation(value = "用户添加出价")
    public void addBid(@PathVariable("productId") Integer productId,
                       @PathVariable("price") Integer price, @RequestHeader("uid") String uid) {


    }
}

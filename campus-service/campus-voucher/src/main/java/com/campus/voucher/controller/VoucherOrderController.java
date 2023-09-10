package com.campus.voucher.controller;

import com.campus.common.util.R;
import com.campus.voucher.domain.VoucherOrder;
import com.campus.voucher.service.VoucherOrderService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: chb
 * @Date: 2023/09/07/17:31
 * @Description:
 */
@RestController
@RequestMapping
public class VoucherOrderController {

    @Autowired
    private VoucherOrderService voucherOrderService;

    @ApiOperation("优惠券秒杀")
    @PostMapping("/seckill/{id}")
    public R seckill(
            @ApiParam(required = true, value = "优惠券id") @PathVariable("id") String id, @RequestHeader("uid") String userId
    ){
        return voucherOrderService.seckill(id,userId);
    }


    @PostMapping("/test")
    public R test(){
        VoucherOrder voucherOrder = new VoucherOrder();
        voucherOrder.setVoucherOrderId("2");
        voucherOrder.setVoucherId("1700349748178624513");
        voucherOrder.setUserId("3");
        voucherOrderService.createOrder(voucherOrder);
        return R.ok();
    }

}

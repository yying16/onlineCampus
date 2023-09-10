package com.campus.voucher.controller;

import com.campus.common.util.R;
import com.campus.voucher.domain.Voucher;
import com.campus.voucher.service.VoucherService;
import com.campus.voucher.vo.VoucherVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: chb
 * @Date: 2023/09/06/20:11
 * @Description:
 */
@RestController
@RequestMapping("/voucher")
public class VoucherController {

    @Autowired
    private VoucherService voucherService;

    /**
     * 创建秒杀优惠券
     * @param voucherVo
     * @return
     */
    @PostMapping("/createSeckillVoucher")
    @ApiOperation("创建秒杀优惠券")
    public R createSeckillVoucher(
            @ApiParam(required = true, value = "秒杀优惠券信息") @RequestBody VoucherVo voucherVo
    ){
        boolean flag = voucherService.createSeckillVoucher(voucherVo);
        if (!flag){
            return R.failed("null","创建失败");
        }
        return R.ok(null, "创建成功");
    }


    @ApiOperation("查看优惠券详情")
    @GetMapping("/getVoucherDetail")
    public R<Voucher> getVoucherDetail(
            @ApiParam(required = true, value = "优惠券id") @RequestParam String voucherId
    ){
        Voucher voucher = voucherService.getVoucherDetail(voucherId);
        return R.ok(voucher);
    }

    @GetMapping("/getVoucherList")
    @ApiOperation("获取优惠券列表")
    public R<List<Voucher>> getVoucherList(){
        List<Voucher> list = voucherService.getVoucherList();
        return R.ok(list);
    }

    /**
     * 创建普通优惠券-------未开发
     * @return
     */
    @PostMapping("/createCommonVoucher")
    @ApiOperation("创建普通优惠券")
    public R createCommonVoucher(){
        return null;
    }
}

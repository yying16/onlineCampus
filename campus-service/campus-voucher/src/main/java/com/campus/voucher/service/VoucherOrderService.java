package com.campus.voucher.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.util.R;
import com.campus.voucher.domain.VoucherOrder;

/**
 * @Author: chb
 * @Date: 2023/09/07/17:29
 * @Description:
 */
public interface VoucherOrderService extends IService<VoucherOrder> {
    R seckill(String id, String userId);

    public void createOrder(VoucherOrder voucherOrder);
}

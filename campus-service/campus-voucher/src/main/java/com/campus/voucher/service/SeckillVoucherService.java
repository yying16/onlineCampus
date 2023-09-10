package com.campus.voucher.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.common.util.R;
import com.campus.voucher.domain.SeckillVoucher;

/**
 * @Author: chb
 * @Date: 2023/09/06/19:57
 * @Description:
 */
public interface SeckillVoucherService extends IService<SeckillVoucher> {
    R seckill(String id, String userId);
}

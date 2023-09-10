package com.campus.voucher.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.util.R;
import com.campus.voucher.dao.SeckillVoucherDao;
import com.campus.voucher.domain.SeckillVoucher;
import com.campus.voucher.service.SeckillVoucherService;
import org.springframework.stereotype.Service;

/**
 * @Author: chb
 * @Date: 2023/09/06/19:58
 * @Description:
 */
@Service
public class SeckillVoucherServiceImpl extends ServiceImpl<SeckillVoucherDao, SeckillVoucher> implements SeckillVoucherService {

    @Override
    public R seckill(String id, String userId) {
        return null;
    }
}

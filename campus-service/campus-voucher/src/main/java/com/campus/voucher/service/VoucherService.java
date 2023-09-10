package com.campus.voucher.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.voucher.domain.Voucher;
import com.campus.voucher.vo.VoucherVo;

import java.util.List;

/**
 * @Author: chb
 * @Date: 2023/09/06/19:57
 * @Description:
 */
public interface VoucherService extends IService<Voucher> {
    boolean createSeckillVoucher(VoucherVo voucherVo);

    Voucher getVoucherDetail(String voucherId);

    List<Voucher> getVoucherList();

}

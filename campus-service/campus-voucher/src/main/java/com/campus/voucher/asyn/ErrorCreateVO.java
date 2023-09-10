package com.campus.voucher.asyn;

import com.campus.voucher.domain.VoucherOrder;
import com.campus.voucher.service.VoucherOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: chb
 * @Date: 2023/09/10/0:06
 * @Description:
 */
@Component
public class ErrorCreateVO {

    @Autowired
    private VoucherOrderService voucherOrderService;

    @Async
    @Transactional
    public void createFail(VoucherOrder voucherOrder, String errorMsg){
        voucherOrder.setErrorMsg(errorMsg);
        voucherOrderService.save(voucherOrder);
    }
}

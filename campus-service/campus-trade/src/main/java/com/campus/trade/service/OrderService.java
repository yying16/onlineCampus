package com.campus.trade.service;

import com.campus.common.util.R;
import com.campus.trade.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.vo.ConfirmOrderForm;
import com.campus.trade.vo.ShowOrder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
* @author xiaolin
* @description 针对表【t_order】的数据库操作Service
* @createDate 2023-07-09 11:38:21
*/
public interface OrderService extends IService<Order> {


    ShowOrder showOrder(String uid, ConfirmOrderForm confirmOrderForm);

    List<ShowOrder> getOrderListByUid(Map<String, Object> searchOrderForm,String uid);

    List<ShowOrder> getOrderListBySellerId(Map<String, Object> searchOrderForm,String sellerId);

    R payOrder(String orderId);
}

package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dao.OrderDao;
import com.campus.trade.domain.Order;
import com.campus.trade.service.OrderService;
import org.springframework.stereotype.Service;

/**
* @author xiaolin
* @description 针对表【t_order】的数据库操作Service实现
* @createDate 2023-07-09 11:38:21
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order>
    implements OrderService{

}





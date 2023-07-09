package com.campus.trade.dao;

import com.campus.trade.domain.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xiaolin
* @description 针对表【t_order】的数据库操作Mapper
* @createDate 2023-07-09 11:38:21
* @Entity com.campus.trade.domain.Order
*/
@Mapper
public interface OrderDao extends BaseMapper<Order> {

}





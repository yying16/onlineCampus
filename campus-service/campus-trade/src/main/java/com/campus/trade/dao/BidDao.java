package com.campus.trade.dao;

import com.campus.trade.domain.Bid;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xiaolin
* @description 针对表【t_bid】的数据库操作Mapper
* @createDate 2023-07-09 11:38:21
* @Entity com.campus.trade.domain.Bid
*/
@Mapper
public interface BidDao extends BaseMapper<Bid> {

}





package com.campus.trade.service;

import com.campus.trade.domain.Bid;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.dto.AddBidForm;

/**
* @author xiaolin
* @description 针对表【t_bid】的数据库操作Service
* @createDate 2023-07-09 11:38:21
*/
public interface BidService extends IService<Bid> {

    boolean addBid(AddBidForm addBidForm, String uid);
}

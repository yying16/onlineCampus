package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.trade.dao.BidDao;
import com.campus.trade.domain.Bid;
import com.campus.trade.dto.AddBidForm;
import com.campus.trade.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author xiaolin
* @description 针对表【t_bid】的数据库操作Service实现
* @createDate 2023-07-09 11:38:21
*/
@Service
public class BidServiceImpl extends ServiceImpl<BidDao, Bid>
    implements BidService{

    @Autowired
    ServiceCenter serviceCenter;




    @Override
    public boolean addBid(AddBidForm addBidForm, String uid) {
        Bid bid = new Bid();
        bid.setPrice(addBidForm.getPrice());
        bid.setProductId(addBidForm.getProductId());
        bid.setUserId(uid);
        String insert = serviceCenter.insert(bid);
        if(insert!=null){
            return true;
        }
        return false;
    }
}





package com.campus.trade.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/17 16:13
 */

@Data
public class ShowBid {

    private String bidId;
    private String productId;
    private String uid;
    private String nickname;
    private String avatar;
    private BigDecimal price;
    private String time;

}

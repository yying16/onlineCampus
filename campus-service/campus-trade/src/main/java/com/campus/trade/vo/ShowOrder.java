package com.campus.trade.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.campus.trade.domain.Order;
import com.campus.trade.domain.Product;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/7/13 15:24
 */
@Data
public class ShowOrder implements Serializable {


    //商品信息
    Product product;

    //总价钱
    BigDecimal totalPrice;

    //地址
    String address;


    //电话
    String telephone;


    //收货人
    String consignee;


    //订单号
    String orderNo;


    //卖家昵称
    String sellerNickName;

    //卖家头像
    String sellerAvatar;

    //下单时间
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String createTime;
}

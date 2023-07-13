package com.campus.trade.vo;

import com.campus.trade.domain.Product;
import lombok.Data;

/**
 * @auther xiaolin
 * @create 2023/7/13 14:53
 */
@Data
public class ConfirmOrderForm {

    Product product;

    String address;

    String telephone;

    String consignee;


}

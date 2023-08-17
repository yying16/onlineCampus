package com.campus.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * author kakakaka
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FavoritesList {
    String favoritesId; // 收藏记录编号
    String username; // 卖家用户昵称
    String avatar; // 卖家用户头像
    String productName; // 商品标题
    BigDecimal productPrice; // 商品价格
    private List<String> images; // 图片列表
    String productDescription; // 商品类型
}

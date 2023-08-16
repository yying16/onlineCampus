package com.campus.trade.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author kakakaka
 */
@Data
@AllArgsConstructor
@NoArgsConstructor

public class FavoritesList {
    String favoritesId; // 收藏记录编号
    String userId; // 用户编号
    String productId; // 商品编号
    String productName; // 商品标题
    Boolean deleted;    // 逻辑删除
}

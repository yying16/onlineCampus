package com.campus.trade.service;

import com.campus.trade.domain.ProductLike;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author xiaolin
* @description 针对表【t_product_like(兼职点赞表)】的数据库操作Service
* @createDate 2023-08-16 15:13:36
*/
public interface ProductLikeService extends IService<ProductLike> {

    String searchLikeIsExist(String userId, String productId);

    void deleteLikeByProductId(String id);
}

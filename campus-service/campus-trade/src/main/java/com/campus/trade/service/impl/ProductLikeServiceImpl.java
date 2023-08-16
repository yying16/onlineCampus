package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dao.ProductLikeDao;
import com.campus.trade.domain.ProductLike;
import com.campus.trade.service.ProductLikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author xiaolin
* @description 针对表【t_product_like(兼职点赞表)】的数据库操作Service实现
* @createDate 2023-08-16 15:13:36
*/
@Service
public class ProductLikeServiceImpl extends ServiceImpl<ProductLikeDao, ProductLike>
    implements ProductLikeService{


    @Autowired
    private ProductLikeDao productLikeDao;

    @Override
    public String searchLikeIsExist(String userId, String productId) {
        return productLikeDao.searchLikeIsExist(userId,productId);
    }

    @Override
    public void deleteLikeByProductId(String id) {
        productLikeDao.deleteLikeByProductId(id);
    }
}





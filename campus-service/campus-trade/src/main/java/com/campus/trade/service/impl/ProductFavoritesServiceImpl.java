package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dao.ProductFavoritesDao;
import com.campus.trade.domain.ProductFavorites;
import com.campus.trade.service.ProductFavoritesService;
import com.campus.trade.vo.FavoritesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author xiaolin
* @description 针对表【t_product_favorites(兼职收藏表)】的数据库操作Service实现
* @createDate 2023-08-16 15:13:36
*/
@Service
public class ProductFavoritesServiceImpl extends ServiceImpl<ProductFavoritesDao, ProductFavorites>
    implements ProductFavoritesService{

    @Autowired
    private ProductFavoritesDao productFavoritesDao;

    @Override
    public String searchFavoritesIsExist(String userId, String productId) {
        return productFavoritesDao.searchFavoritesIsExist(userId,productId);
    }

    @Override
    public List<FavoritesList> SearchFavoritesByUserId(String userId) {
        return productFavoritesDao.SearchFavoritesByUserId(userId);
    }

    @Override
    public void deleteFavoritesByProductId(String id) {
        productFavoritesDao.deleteFavoritesByProductId(id);
    }
}





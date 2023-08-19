package com.campus.trade.service;

import com.campus.trade.domain.ProductFavorites;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.vo.FavoritesList;

import java.util.List;

/**
* @author xiaolin
* @description 针对表【t_product_favorites(兼职收藏表)】的数据库操作Service
* @createDate 2023-08-16 15:13:36
*/
public interface ProductFavoritesService extends IService<ProductFavorites> {

    String searchFavoritesIsExist(String userId, String productId);

    List<FavoritesList> SearchFavoritesByUserId(String userId);

    void deleteFavoritesByProductId(String id);
}

package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.dao.ProductFavoritesDao;
import com.campus.trade.domain.Image;
import com.campus.trade.domain.Product;
import com.campus.trade.domain.ProductFavorites;
import com.campus.trade.feign.UserClient;
import com.campus.trade.service.ImageService;
import com.campus.trade.service.ProductFavoritesService;
import com.campus.trade.vo.FavoritesList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private ServiceCenter serviceCenter;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserClient userClient;

    @Override
    public String searchFavoritesIsExist(String userId, String productId) {
        return productFavoritesDao.searchFavoritesIsExist(userId,productId);
    }

    @Override
    public List<FavoritesList> SearchFavoritesByUserId(String userId) {
        List<FavoritesList> favoritesLists = new ArrayList<>();
        //根据用户id查询收藏列表
        QueryWrapper<ProductFavorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<ProductFavorites> productFavorites = productFavoritesDao.selectList(queryWrapper);
        if (productFavorites.size() == 0){
            return null;
        }
        for(ProductFavorites favorites:productFavorites){
            FavoritesList favoritesList = new FavoritesList();
            favoritesList.setFavoritesId(favorites.getFavoritesId());
            favoritesList.setProductName(favorites.getProductName());
            String productId = favorites.getProductId();
            String theuserId = favorites.getUserId();
            //根据商品id查询商品信息
            Product product = (Product)serviceCenter.selectMySql(productId, Product.class);
            favoritesList.setProductDescription(product.getDescription());
            favoritesList.setProductPrice(product.getPrice());
            //根据商品id查询商品图片
            QueryWrapper<Image> imageQueryWrapper = new QueryWrapper<>();
            imageQueryWrapper.eq("other_id",productId);
            List<Image> images = imageService.list(imageQueryWrapper);
            List<String> imageUrls = new ArrayList<>();
            for (Image image : images) {
                imageUrls.add(image.getImgUrl());
            }

            favoritesList.setImages(imageUrls);
            //根据卖家id查询卖家信息
            R userById = userClient.getUserById(product.getUserId());
            Map<String, Object> data = (Map<String, Object>) userById.getData();
            favoritesList.setUsername((String) data.get("username"));

            //调用user服务获取用户头像
            favoritesList.setAvatar((String) data.get("userImage"));
            favoritesLists.add(favoritesList);
        }
        return favoritesLists;
    }

    @Override
    public void deleteFavoritesByProductId(String id) {
        productFavoritesDao.deleteFavoritesByProductId(id);
    }
}





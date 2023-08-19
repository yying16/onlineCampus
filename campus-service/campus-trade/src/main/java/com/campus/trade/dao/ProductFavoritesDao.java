package com.campus.trade.dao;

import com.campus.trade.domain.ProductFavorites;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.trade.vo.FavoritesList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author xiaolin
* @description 针对表【t_product_favorites(兼职收藏表)】的数据库操作Mapper
* @createDate 2023-08-16 15:13:36
* @Entity com.campus.trade.domain.ProductFavorites
*/
@Mapper
public interface ProductFavoritesDao extends BaseMapper<ProductFavorites> {


    @Select("select favorites_id from t_product_favorites where user_id=#{userId} and product_id=#{productId} and deleted=false")
    String searchFavoritesIsExist(String userId, String productId);



    @Update("update t_product_favorites set deleted=1 where product_id = #{productId}")
    void deleteFavoritesByProductId(String productId);

    @Select("select user_id from t_product_favorites where product_id=#{productId}")
    List <String> selectCollectorsByJobId(String productId);

}





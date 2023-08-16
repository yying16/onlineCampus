package com.campus.trade.dao;

import com.campus.trade.domain.ProductLike;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author xiaolin
* @description 针对表【t_product_like(兼职点赞表)】的数据库操作Mapper
* @createDate 2023-08-16 15:13:36
* @Entity com.campus.trade.domain.ProductLike
*/
@Mapper
public interface ProductLikeDao extends BaseMapper<ProductLike> {

    @Select("select like_id from t_product_like where user_id=#{userId} and product_id=#{productId} and deleted=false")
    String searchLikeIsExist(String userId,String productId); // 根据用户和兼职id查询点赞记录是否存在


    @Update("update t_product_like set deleted=1 where product_id=#{productId}")
    void deleteLikeByProductId(String productId);

}





package com.campus.trade.dao;

import com.campus.trade.domain.Product;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author xiaolin
* @description 针对表【t_product】的数据库操作Mapper
* @createDate 2023-07-09 11:33:17
* @Entity com.campus.trade.domain.Product
*/
@Mapper
public interface ProductDao extends BaseMapper<Product> {


}





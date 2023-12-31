package com.campus.trade.service;

import com.campus.trade.domain.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.dto.AddProductForm;
import com.campus.trade.vo.ShowProduct;
import io.swagger.models.auth.In;

import java.util.List;
import java.util.Map;

/**
* @author xiaolin
* @description 针对表【t_product】的数据库操作Service
* @createDate 2023-07-09 11:33:17
*/
public interface ProductService extends IService<Product> {

    boolean addProduct(AddProductForm addProductForm);

    List<ShowProduct> listProduct(Map<String,Object> searchProductForm);

    ShowProduct getByTheId(String id);

    List<ShowProduct> IndexlistProduct(Integer offset);
}

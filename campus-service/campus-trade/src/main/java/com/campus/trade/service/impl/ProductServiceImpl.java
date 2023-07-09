package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.trade.dao.ProductDao;
import com.campus.trade.domain.Product;
import com.campus.trade.dto.ProductForm;
import com.campus.trade.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* @author xiaolin
* @description 针对表【t_product】的数据库操作Service实现
* @createDate 2023-07-09 11:33:17
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product>
    implements ProductService{

    @Autowired
    private ServiceCenter serviceCenter;

    @Override
    public boolean addProduct(ProductForm productForm) {
        //将productForm转换为product
        Product product = new Product();
        BeanUtils.copyProperties(productForm,product);
        String insert = serviceCenter.insert(product);
//        int insert = baseMapper.insert(product);
        if(insert!=null){
            return true;
        }else{
            return false;
        }
    }
}





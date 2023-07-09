package com.campus.trade.controller;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Product;
import com.campus.trade.dto.ProductForm;
import com.campus.trade.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @auther xiaolin
 * @create 2023/7/9 12:23
 */
@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;


    @Autowired
    private ServiceCenter serviceCenter;




    //添加商品
    @PostMapping("/addProduct")
    public R addProduct(@RequestBody ProductForm productForm){
        boolean b = productService.addProduct(productForm);
        if(b){
            return R.ok();
        }else{
            return R.failed();
        }
    }

    //根据商品id删除商品
    @DeleteMapping("{id}")
    public R deleteProduct(@PathVariable("id") String id){
        boolean b = productService.removeById(id);
        if(b){
            return R.ok();
        }else{
            return R.failed();
        }
    }

    //根据商品id查询商品
    @GetMapping("{id}")
    public R getProduct(@PathVariable("id") String id){
        Product product = (Product) serviceCenter.search(id, Product.class);
//        Product product = productService.getById(id);
        if(product!=null){
            return R.ok(product);
        }else{
            return R.failed();
        }
    }



}

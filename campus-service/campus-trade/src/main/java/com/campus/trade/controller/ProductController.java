package com.campus.trade.controller;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Product;
import com.campus.trade.dto.AddProductForm;
import com.campus.trade.service.ProductService;
import com.campus.trade.vo.ShowProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.models.auth.In;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @auther xiaolin
 * @create 2023/7/9 12:23
 */
@RestController
@RequestMapping("/product")
@Api(tags = "商品管理")
public class ProductController {

    @Autowired
    private ProductService productService;


    @Autowired
    private ServiceCenter serviceCenter;


    //查看商品列表（条件懒加载）
    @ApiOperation(value = "查看商品列表（条件懒加载）")
    @PostMapping("/list/{page}/{size}")
    public R listProduct(@ApiParam("页码") @PathVariable("page") long page,@ApiParam("一页展示的数据条数") @PathVariable("size") long size,@ApiParam("查询条件") @RequestBody Map<String, Object> searchProductForm){

        // 根据页码和每页数据量计算偏移量
        long offset = (page - 1) * size;
        searchProductForm.put("limit",offset+" "+size);
        List<ShowProduct> products =  productService.listProduct(searchProductForm);

        return R.ok(products,"查询商品列表成功");
    }


    //卖家发布商品
    @PostMapping("/addProduct")
    @ApiOperation(value = "卖家发布商品")
    public R addProduct(@RequestBody AddProductForm addProductForm){
        boolean b = productService.addProduct(addProductForm);
        if(b){
            return R.ok(null,"发布商品成功");
        }else{
            return R.failed(null,"发布商品失败");
        }
    }

    //根据商品id删除商品
    @DeleteMapping("{id}")
    @ApiOperation(value = "根据商品id删除商品")
    public R deleteProduct(@PathVariable("id") String id){

//        //拿到商品信息json字符串
//        Object search = serviceCenter.search(id, Product.class);
//
//        //将json字符串转换为商品对象
//        Product product = new Gson().fromJson(search.toString(), Product.class);
//
//        boolean delete = serviceCenter.delete(product);

        boolean delete = serviceCenter.delete(id, Product.class);

        if(delete){
            return R.ok(null,"删除商品成功");
        }else{
            return R.failed(null,"删除商品失败");
        }
    }

    //根据商品id查询商品详细信息
    @GetMapping("{id}")
    @ApiOperation(value = "根据商品id查询商品详细信息")
    public R getProduct(@PathVariable("id") String id){

        ShowProduct showProduct = productService.getByTheId(id);

        return R.ok(showProduct,"查询商品详细信息成功");

    }


    //根据商品id修改商品信息
    @PutMapping("{id}")
    @ApiOperation(value = "根据商品id修改商品信息")
    public R updateProduct(@PathVariable("id") String id,@RequestBody AddProductForm addProductForm){
        Product product = productService.getById(id);
        if(product == null){
            return R.failed(null,"商品不存在");
        }
        BeanUtils.copyProperties(addProductForm,product);
        boolean update = serviceCenter.update(product);
        if(update){
            return R.ok(null,"修改商品信息成功");
        }else{
            return R.failed(null,"修改商品信息失败");
        }
    }

    //根据用户id查看发布的商品列表
    @PostMapping("/listByUserId/{page}/{size}/{userId}")
    @ApiOperation(value = "根据用户id查看发布的商品列表")
    public R listProductByUserId(@ApiParam("页码") @PathVariable("page") long page,@ApiParam("一页展示的数据条数") @PathVariable("size") long size,@RequestBody Map<String, Object> searchProductForm,@PathVariable("userId") String userId){
        // 根据页码和每页数据量计算偏移量
        long offset = (page - 1) * size;
        searchProductForm.put("limit",offset+" "+size);
        searchProductForm.put("userId",userId);
        List<Product> products =  serviceCenter.search(searchProductForm,Product.class);

        return R.ok(products,"查询商品列表成功");
    }


}

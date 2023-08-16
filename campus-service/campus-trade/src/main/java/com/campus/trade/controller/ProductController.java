package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Image;
import com.campus.trade.domain.Product;
import com.campus.trade.dto.AddProductForm;
import com.campus.trade.dto.SearchProductForm;
import com.campus.trade.service.*;
import com.campus.trade.vo.ShowProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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
    private ImageService imageService;

    @Autowired
    private ServiceCenter serviceCenter;

    @Autowired
    private CategoryService categoryService;


    @Autowired
    private ProductFavoritesService productFavoritesService;

    @Autowired
    private ProductLikeService productLikeService;

    //查看商品列表（条件懒加载）
    @ApiOperation(value = "带查询条件的查看商品列表（条件懒加载）")
    @PostMapping("/listByQuery/{offset}")
    public R listProduct(@ApiParam("已展示的数据条数") @PathVariable("offset") Integer offset,@ApiParam("查询条件") @RequestBody SearchProductForm searchProductForm){

        // 根据页码和每页数据量计算偏移量
//        long offset = (page - 1) * size;
//        searchProductForm.put("limit",offset+" "+size);
        String name = searchProductForm.getSearchcontent();

        Map<String, Object> searchProductMap= new HashMap<>();
        searchProductMap.put("description",name);
        searchProductMap.put("limit",offset+" "+10);
        List<ShowProduct> products =  productService.listProduct(searchProductMap);

        return R.ok(products,"查询商品列表成功");
    }

    //查看商品列表（条件懒加载）
    @ApiOperation(value = "首页查看商品列表（懒加载）")
    @PostMapping("/list/{offset}")
    public R listProduct(@ApiParam("已展示的数据条数") @PathVariable("offset") Integer offset){

        List<ShowProduct> products =  productService.IndexlistProduct(offset);

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
            //处理点赞记录:逻辑删除与当前job绑定的所有like记录
            productLikeService.deleteLikeByProductId(id);

            //处理收藏记录:逻辑删除与当前job绑定的所有favorites记录,并用后端调用message模块的sendPromptInformation方法发送提示信息给申请者
            productFavoritesService.deleteFavoritesByProductId(id);

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

        //修改商品信息后，修改图片信息
        //根据商品id查询图片信息
        QueryWrapper<Image> wrapper = new QueryWrapper<>();
        wrapper.eq("other_id",id);
        //先删除原来的图片信息
        imageService.remove(wrapper);
        //再添加新的图片信息
        List<String> images = addProductForm.getImages();
        for (String image : images) {
            Image image1 = new Image();
            image1.setOtherId(id);
            image1.setImgUrl(image);
            image1.setOtherType("product");
            imageService.save(image1);
        }

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

        //根据商品id查询商品图片
        for (Product product : products){
            String productId = product.getProductId();
            QueryWrapper<Image> imageQueryWrapper = new QueryWrapper<>();
            imageQueryWrapper.eq("other_id",productId);
            List<Image> images = imageService.list(imageQueryWrapper);
            List<String> imageUrls = new ArrayList<>();
            for (Image image : images) {
                imageUrls.add(image.getImgUrl());
            }
            product.setImages(imageUrls);
        }

        return R.ok(products,"查询商品列表成功");
    }


}

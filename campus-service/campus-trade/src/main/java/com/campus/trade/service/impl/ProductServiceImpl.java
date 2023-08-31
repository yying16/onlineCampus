package com.campus.trade.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.dao.ProductDao;
import com.campus.trade.domain.Category;
import com.campus.trade.domain.Image;
import com.campus.trade.domain.Product;
import com.campus.trade.dto.AddProductForm;
import com.campus.trade.feign.UserClient;
import com.campus.trade.service.CategoryService;
import com.campus.trade.service.ImageService;
import com.campus.trade.service.ProductService;
import com.campus.trade.vo.ShowProduct;
import org.apache.catalina.User;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* @author xiaolin
* @description 针对表【t_product】的数据库操作Service实现
* @createDate 2023-07-09 11:33:17
*/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductDao, Product>
    implements ProductService{

    @Autowired
    private UserClient userClient;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ServiceCenter serviceCenter;


    @Autowired
    private RedissonClient redissonClient;

    @Override
    public boolean addProduct(AddProductForm addProductForm) {

        RLock addProductLock = redissonClient.getLock("addProduct"+addProductForm.getUserId());

        try {
            addProductLock.tryLock(6000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //将productForm转换为product
        Product product = new Product();
        BeanUtils.copyProperties(addProductForm,product);
        String insert = serviceCenter.insert(product);
//        int insert = baseMapper.insert(product);

        addProductLock.unlock();

        if(insert!=null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<ShowProduct> listProduct(Map<String,Object> searchProductForm) {
        List<Product> search = serviceCenter.search(searchProductForm, Product.class);

        List<ShowProduct> showProducts = new ArrayList<>();

        //封装数据
        for (Product product : search) {
            String productId = product.getProductId();
            QueryWrapper<Image> imageQueryWrapper = new QueryWrapper<>();
            imageQueryWrapper.eq("other_id",productId);
            List<Image> images = imageService.list(imageQueryWrapper);
            List<String> imageUrls = new ArrayList<>();
            for (Image image : images) {
                imageUrls.add(image.getImgUrl());
            }
            product.setImages(imageUrls);

            ShowProduct showProduct = getShowProduct(product);

            showProducts.add(showProduct);
        }
        return showProducts;
    }


    //封装数据
    public ShowProduct getShowProduct(Product product){
        ShowProduct showProduct = new ShowProduct();
        BeanUtils.copyProperties(product,showProduct);

        //调用user服务获取用户信息
        String userId = product.getUserId();
        R userById = userClient.getUserById(userId);
        System.out.println("userId:========"+userId);
        System.out.println("userById:========"+userById);
        Map<String, Object> data = (Map<String, Object>) userById.getData();
        showProduct.setUserName((String) data.get("username"));

        //调用user服务获取用户头像
        String userAvatar = (String) data.get("userImage");
        showProduct.setUserAvatar(userAvatar);

        //分类名
        String categoryId = product.getCategoryId();
        Category category = categoryService.getById(categoryId);
        showProduct.setCategoryName(category.getName());
        Category parentCategory = categoryService.getById(category.getParentId());
        showProduct.setSubCategoryName(parentCategory.getName());

        //根据商品id查询商品图片
//        String productId = product.getProductId();
//        QueryWrapper<Image> imageQueryWrapper = new QueryWrapper<>();
//        imageQueryWrapper.eq("other_id",productId);
//        List<Image> images = imageService.list(imageQueryWrapper);
//        List<String> imageUrls = new ArrayList<>();
//        for (Image image : images) {
//            imageUrls.add(image.getImgUrl());
//        }
//        showProduct.setImageUrls(imageUrls);

        return showProduct;
    }



    @Override
    public ShowProduct getByTheId(String id) {
        Product search = (Product) serviceCenter.selectMySql(id, Product.class);
        if (search == null) {
            return null;
        }
//        Product product = new Gson().fromJson(search.toString(), Product.class);
        ShowProduct showProduct = getShowProduct(search);

        return showProduct;
    }

    @Override
    public List<ShowProduct> IndexlistProduct(Integer offset) {

        List<Product> search = serviceCenter.loadData(offset, Product.class);

        if (search==null){
            return null;
        }

        List<ShowProduct> showProducts = new ArrayList<>();

        //封装数据
        for (Product product : search) {
            String productId = product.getProductId();
            QueryWrapper<Image> imageQueryWrapper = new QueryWrapper<>();
            imageQueryWrapper.eq("other_id",productId);
            List<Image> images = imageService.list(imageQueryWrapper);
            List<String> imageUrls = new ArrayList<>();
            for (Image image : images) {
                imageUrls.add(image.getImgUrl());
            }
            product.setImages(imageUrls);

            ShowProduct showProduct = getShowProduct(product);

            showProducts.add(showProduct);
        }
        return showProducts;
    }
}





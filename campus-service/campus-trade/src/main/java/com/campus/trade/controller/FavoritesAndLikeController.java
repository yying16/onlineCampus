package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Product;
import com.campus.trade.domain.ProductFavorites;
import com.campus.trade.domain.ProductLike;
import com.campus.trade.service.ProductFavoritesService;
import com.campus.trade.service.ProductLikeService;
import com.campus.trade.vo.FavoritesList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @auther xiaolin
 * @create 2023/8/16 15:18
 */
@RestController
@RequestMapping("/favoritesAndLike")
@Api(tags = "收藏和点赞服务")
public class FavoritesAndLikeController {

    @Autowired
    private ServiceCenter serviceCenter;


    @Autowired
    private ProductLikeService productLikeService;


    @Autowired
    private ProductFavoritesService productFavoritesService;

    @Autowired
    private RedissonClient redissonClient;

    @ApiOperation("用户点赞操作")
    @GetMapping("/likeProduct/{productId}")
    public R likeProduct(@RequestHeader("uid")String userId, @PathVariable("productId") String productId){

        RLock likeProductLock = redissonClient.getLock("likeProduct"+userId+"-"+productId);

        try {
            likeProductLock.tryLock(6000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Product product = (Product)serviceCenter.selectMySql(productId,Product.class);
        if(product==null){
            likeProductLock.unlock();
            return R.failed(null,"当前商品记录不存在，无法点赞");
        }
        String likeId = productLikeService.searchLikeIsExist(userId,productId);
        if(likeId==null){
            ProductLike like = new ProductLike();
            like.setLikeId(IdWorker.getIdStr(like));
            like.setUserId(userId);
            like.setProductId(productId);
            if(serviceCenter.insertMySql(like)){
                product.setLikeNum(product.getLikeNum()+1);
                if(!serviceCenter.updateMySql(product)){
                    likeProductLock.unlock();
                    return R.failed(null,"更新商品信息失败");
                }

                likeProductLock.unlock();
                return R.ok(null,"点赞成功");
            }
        }
        likeProductLock.unlock();
        return R.failed(null,"您已为该商品点赞了，是否需要取消点赞？");
    }

    @ApiOperation("用户取消点赞操作")
    @GetMapping("/cancelLikeProduct/{productId}")
    public R cancelLikeProduct(@RequestHeader("uid")String userId,@PathVariable("productId") String productId){

        RLock cancelLikeProductLock = redissonClient.getLock("cancelLikeProduct"+userId+"-"+productId);

        try {
            cancelLikeProductLock.tryLock(6000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Product product = (Product)serviceCenter.selectMySql(productId,Product.class);
        // 判断该用户点赞的商品记录是否被删除
        if(product==null){
            cancelLikeProductLock.unlock();
            return R.failed(null,"当前商品记录不存在");
        }
        // 商品未被删除
        String likeId = productLikeService.searchLikeIsExist(userId,productId); // 查找对应的点赞记录Id
        // 若该用户点赞过该兼职，则取消点赞
        if(likeId!=null){
            ProductLike like = (ProductLike) serviceCenter.selectMySql(likeId,ProductLike.class); // 通过id查找该点赞记录

            if (like==null){
                cancelLikeProductLock.unlock();
                return R.failed(null,"点赞信息不存在");
            }

            boolean delete = serviceCenter.delete(likeId,ProductLike.class);// 删除点赞记录
            if(!delete){
                cancelLikeProductLock.unlock();
                return R.failed(null,"取消点赞失败");
            }

            //修改商品记录中的likeNum
            product.setLikeNum(product.getLikeNum()-1);
            if(!serviceCenter.updateMySql(product)){ // 存入数据库中
                cancelLikeProductLock.unlock();
                return R.failed(null,"更新商品信息失败");
            }
            cancelLikeProductLock.unlock();
            return R.ok(null,"取消点赞成功");
        }else{
            cancelLikeProductLock.unlock();
            return R.failed(null,"取消点赞失败");
        }
    }


    @ApiOperation("用户收藏操作")
    @GetMapping("/favoritesProduct/{productId}")
    public R FavoritesProduct(@RequestHeader("uid")String userId,@PathVariable("productId") String productId){

        RLock FavoritesProductLock = redissonClient.getLock("FavoritesProduct"+userId+"-"+productId);

        try {
            FavoritesProductLock.tryLock(6000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        Product product = (Product) serviceCenter.selectMySql(productId,Product.class);
        if(product==null){
            FavoritesProductLock.unlock();
            return R.failed(null,"当前兼职记录不存在，无法收藏");
        }
        String favoritesId = productFavoritesService.searchFavoritesIsExist(userId,productId);
        if(favoritesId==null){ // 若没有存在收藏记录，则新建收藏记录
            ProductFavorites favorites = new ProductFavorites();
            favorites.setFavoritesId(IdWorker.getIdStr(favorites));
            favorites.setUserId(userId);
            favorites.setProductName(product.getName());
            favorites.setProductId(productId);
            if(serviceCenter.insertMySql(favorites)){ // 将收藏记录存入数据库
                product.setFavoritesNum(product.getFavoritesNum()+1); // 修改对应商品记录的收藏人数
                if(!serviceCenter.updateMySql(product)){ // 将修改后的商品记录更新到数据库
                    FavoritesProductLock.unlock();
                    return R.failed(null,"更新商品信息失败");
                }
                FavoritesProductLock.unlock();
                return R.ok(null, "收藏成功");
            }

        }
        FavoritesProductLock.unlock();
        return R.failed(null,"您已收藏过该商品了，是否需要取消收藏？");
    }

    @ApiOperation("用户取消收藏操作")
    @GetMapping("/cancelFavoritesProduct/{productId}")
    public R cancelFavoritesProduct(@RequestHeader("uid")String userId,@PathVariable("productId") String productId){

        RLock cancelFavoritesProductLock = redissonClient.getLock("cancelFavoritesProduct"+userId+"-"+productId);

        try {
            cancelFavoritesProductLock.tryLock(6000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Product product = (Product) serviceCenter.selectMySql(productId,Product.class);
        // 判断该用户收藏的兼职记录是否被删除
        if(product==null){
            cancelFavoritesProductLock.unlock();
            return R.failed(null,"当前兼职记录不存在");
        }
        // 兼职未被删除
        String favoritesId = productFavoritesService.searchFavoritesIsExist(userId,productId); // 查找对应的收藏记录Id
        // 若该用户收藏过该兼职，则取消收藏
        if(favoritesId!=null){
            ProductFavorites favorites = (ProductFavorites) serviceCenter.selectMySql(favoritesId,ProductFavorites.class); // 通过id查找该收藏记录

            if (favorites==null){
                cancelFavoritesProductLock.unlock();
                return R.failed(null,"收藏信息不存在");
            }

            boolean delete = serviceCenter.delete(favoritesId,ProductFavorites.class);// 删除收藏记录
            if(!delete){
                cancelFavoritesProductLock.unlock();
                return R.failed(null,"取消收藏失败");
            }

            //修改job记录中的favoritesNum
            product.setFavoritesNum(product.getFavoritesNum()-1);
            if(!serviceCenter.updateMySql(product)){ // 存入数据库中
                cancelFavoritesProductLock.unlock();
                return R.failed(null,"更新兼职信息失败");
            }
            cancelFavoritesProductLock.unlock();
            return R.ok(null,"取消收藏成功");
        }else {
            cancelFavoritesProductLock.unlock();
            return R.failed(null,"取消收藏失败");
        }
    }

    @ApiOperation("查看用户收藏列表")
    @GetMapping("/searchFavoritesList")
    public R searchFavoritesList(@RequestHeader("uid") String userId) {
        List<FavoritesList> favoritesList = productFavoritesService.SearchFavoritesByUserId(userId);
        if (favoritesList == null || favoritesList.size() == 0) {
            return R.failed(null, "您还没有收藏任何商品");
        }
        return R.ok(favoritesList, "查询成功");
    }


    @ApiOperation("新增商品访问量")
    @GetMapping("/addVisitNum/{productId}")
    public R incrementVisitNum(@PathVariable("productId") String productId){

        RLock addVisitNumLock = redissonClient.getLock("addVisitNum"+productId);

        try {
            addVisitNumLock.tryLock(6000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if(serviceCenter.increment(productId,Product.class,true,"visitNum")){
            addVisitNumLock.unlock();
            return R.ok(null,"访问量增加成功");
        }
        addVisitNumLock.unlock();
        return R.failed(null,"访问量增加失败");
    }


}

package com.campus.trade.controller;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.common.util.SpringContextUtil;
import com.campus.trade.domain.Bid;
import com.campus.trade.dto.AddBidForm;
import com.campus.trade.feign.UserClient;
import com.campus.trade.service.BidService;
import com.campus.trade.vo.ShowBid;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @auther xiaolin
 * @create 2023/7/9 14:30
 */
@RestController
@RequestMapping("/bid")
@Api(tags = "出价管理")
public class BidController {


    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    BidService bidService;


    @Autowired
    UserClient userClient;

//    @Autowired
//    private RedissonClient redissonClient;

    //用户添加出价
    @PostMapping("/addBid")
    @ApiOperation(value = "用户添加出价")
    public R addBid(@RequestBody AddBidForm addBidForm, @RequestHeader("uid") String uid) {

        RedissonClient redissonClient = ((RedissonClient) SpringContextUtil.getBean("redissonClient"));

        RLock addBidLock = redissonClient.getLock("addBid"+addBidForm.getProductId()+uid);


        try {
            addBidLock.tryLock(60,10, TimeUnit.SECONDS);
            boolean b = bidService.addBid(addBidForm, uid);


            if(b){
                addBidLock.unlock();

                return R.ok(null,"出价成功");
            }else {
                addBidLock.unlock();

                return R.failed(null,"出价失败");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    //删除出价
    @DeleteMapping("/{bidId}")
    @ApiOperation(value = "删除出价")
    public R deleteBid(@PathVariable("bidId") String bidId,@RequestHeader("uid") String uid) {

        RedissonClient redissonClient = ((RedissonClient) SpringContextUtil.getBean("redissonClient"));

        RLock deleteBidLock = redissonClient.getLock("deleteBid"+bidId+uid);


        try {
            deleteBidLock.tryLock(6000,1500, TimeUnit.SECONDS);
            Bid bid = (Bid) serviceCenter.search(bidId, Bid.class);
            if(!bid.getUserId().equals(uid)){
                deleteBidLock.unlock();
                return R.failed(null,"无权限删除");
            }
            boolean b = serviceCenter.delete(bidId, Bid.class);

            if(b){
                deleteBidLock.unlock();

                return R.ok(null,"删除成功");
            }else {
                deleteBidLock.unlock();

                return R.failed(null,"删除失败");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



    }

    //查看某个商品的出价列表
    @GetMapping("/list/{productId}")
    @ApiOperation(value = "查看出价列表")
    public R listBid(@PathVariable("productId") String productId) {


        Map<String, Object> searchBidForm = new HashMap<>();
        searchBidForm.put("productId", productId);
        List<Bid> search = serviceCenter.search(searchBidForm, Bid.class);

        if (search == null) {
            return R.failed(null, "查询出价列表失败");
        }

        List<ShowBid> showBids = new ArrayList<>();
        for(Bid bid :search){
            ShowBid showBid = new ShowBid();
            showBid.setBidId(bid.getBidId());
            showBid.setPrice(bid.getPrice());
            showBid.setProductId(bid.getProductId());
            showBid.setUid(bid.getUserId());
            showBid.setTime(bid.getUpdateTime());

            //查询用户信息
            R user = userClient.getUserById(bid.getUserId());
            Map<String, Object> data = (Map<String, Object>) user.getData();
            showBid.setNickname((String) data.get("username"));
            showBid.setAvatar((String) data.get("userImage"));
            showBids.add(showBid);
        }
        return R.ok(showBids,"查询出价列表成功");
    }

    //修改出价
    @PutMapping("/{bidId}/{price}")
    @ApiOperation(value = "修改出价")
    public R updateBid(@PathVariable("bidId") String bidId,@PathVariable("price") BigDecimal price,@RequestHeader("uid") String uid) {
        RedissonClient redissonClient = ((RedissonClient) SpringContextUtil.getBean("redissonClient"));

        RLock updateBidLock = redissonClient.getLock("updateBid"+bidId+uid);


        try {
            updateBidLock.tryLock(60,10, TimeUnit.SECONDS);
            Bid bid = (Bid) serviceCenter.search(bidId, Bid.class);

            if(!bid.getUserId().equals(uid)){
                updateBidLock.unlock();

                return R.failed(null,"无权限修改");
            }

            bid.setPrice(price);
            boolean update = serviceCenter.update(bid);

            if(update){
                updateBidLock.unlock();

                return R.ok(null,"修改出价成功");
            }else {
                updateBidLock.unlock();

                return R.failed(null,"修改出价失败");
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



    }

    //查看用户的出价列表
    @GetMapping("/listByUser")
    @ApiOperation(value = "查看出价列表")
    public R listBidByUser(@RequestHeader("uid") String uid) {


        Map<String, Object> searchBidForm = new HashMap<>();
        searchBidForm.put("userId", uid);
        List<Bid> search = serviceCenter.search(searchBidForm, Bid.class);

        if (search == null) {
            return R.failed(null, "查询出价列表失败");
        }

        List<ShowBid> showBids = new ArrayList<>();
        for(Bid bid :search){
            ShowBid showBid = new ShowBid();
            showBid.setBidId(bid.getBidId());
            showBid.setPrice(bid.getPrice());
            showBid.setProductId(bid.getProductId());
            showBid.setUid(bid.getUserId());
            showBid.setTime(bid.getUpdateTime());

            //查询用户信息
            R user = userClient.getUserById(bid.getUserId());
            Map<String, Object> data = (Map<String, Object>) user.getData();
            showBid.setNickname((String) data.get("username"));
            showBid.setAvatar((String) data.get("userImage"));
            showBids.add(showBid);
        }
        return R.ok(showBids,"查询出价列表成功");
    }

}

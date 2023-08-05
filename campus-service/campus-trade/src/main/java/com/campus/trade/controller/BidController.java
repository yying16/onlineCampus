package com.campus.trade.controller;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Bid;
import com.campus.trade.dto.AddBidForm;
import com.campus.trade.service.BidService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    //用户添加出价
    @PostMapping("/addBid")
    @ApiOperation(value = "用户添加出价")
    public R addBid(@RequestBody AddBidForm addBidForm, @RequestHeader("uid") String uid) {
        boolean b = bidService.addBid(addBidForm, uid);
        if(b){
            return R.ok(null,"出价成功");
        }else {
            return R.failed(null,"出价失败");
        }
    }

    //删除出价
    @DeleteMapping("/{bidId}")
    @ApiOperation(value = "删除出价")
    public R deleteBid(@PathVariable("bidId") String bidId) {
        boolean b = serviceCenter.delete(bidId, Bid.class);
        if(b){
            return R.ok(null,"删除成功");
        }else {
            return R.failed(null,"删除失败");
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
        return R.ok(search,"查询出价列表成功");
    }

    //修改出价
    @PutMapping("/{bidId}/{price}")
    @ApiOperation(value = "修改出价")
    public R updateBid(@PathVariable("bidId") String bidId,@PathVariable("price") BigDecimal price) {
        Bid bid = (Bid) serviceCenter.search(bidId, Bid.class);
        bid.setPrice(price);
        boolean update = serviceCenter.update(bid);
        if(update){
            return R.ok(null,"修改出价成功");
        }else {
            return R.failed(null,"修改出价失败");
        }
    }
}

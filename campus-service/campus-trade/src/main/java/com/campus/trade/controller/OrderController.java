package com.campus.trade.controller;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Order;
import com.campus.trade.domain.Product;
import com.campus.trade.dto.SearchOrderForm;
import com.campus.trade.vo.ConfirmOrderForm;
import com.campus.trade.feign.UserClient;
import com.campus.trade.service.OrderService;
import com.campus.trade.service.ProductService;
import com.campus.trade.vo.ShowOrder;
import com.campus.trade.vo.ShowProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
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
@RequestMapping("/order")
@Api(tags = "订单服务")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ServiceCenter serviceCenter;

    @Autowired
    private UserClient userClient;

    @Autowired
    private RedissonClient redissonClient;


    //查询下单前确认信息（用户和商品信息）
    @GetMapping("/confirm/{productId}")
    @ApiOperation("查询下单前确认信息（用户和商品信息）")
    public R confirmOrder(@ApiParam("商品id") @PathVariable("productId") String productId, @RequestHeader("uid") String uid) {
        //查询商品信息
//        Product product = productService.getById(productId);
//        Product product = (Product) serviceCenter.search(productId, Product.class);
        Product search = (Product) serviceCenter.search(productId, Product.class);
        if (search == null) {
            return R.failed(null, "商品不存在");
        }
//        Product product = new Gson().fromJson(search.toString(), Product.class);


        R user = userClient.getUserById(uid);
        //收货地址
        Map<String, Object> data = (Map<String, Object>) user.getData();
        String address = (String) data.get("address");
        //手机号
        String telephone = (String) data.get("telephone");
        //收货人
        String consignee = (String) data.get("consignee");

        ConfirmOrderForm confirmOrderForm = new ConfirmOrderForm();
        confirmOrderForm.setProduct(search);
        confirmOrderForm.setAddress(address);
        confirmOrderForm.setTelephone(telephone);
        confirmOrderForm.setConsignee(consignee);
        return R.ok(confirmOrderForm, "查询成功");
    }

    //展示生成的订单信息
    @PostMapping("/createOrder")
    @ApiOperation("展示生成的订单信息")
    public R createOrder(@RequestBody ConfirmOrderForm confirmOrderForm, @RequestHeader("uid") String uid) {
        ShowOrder showOrder = orderService.showOrder(uid, confirmOrderForm);
        if (showOrder == null) {
            return R.failed(null, "订单生成失败");
        }
        return R.ok(showOrder, "订单生成成功");
    }


    //修改订单状态
    @PutMapping("/updateOrderStatus/{orderId}/{status}")
    @ApiOperation("修改订单状态")
    public R updateOrderStatus(@PathVariable("orderId") String orderId,@PathVariable("status") Integer status) {
        RLock updateOrderStatusLock = redissonClient.getLock("updateOrderStatus"+orderId);

        Order order = (Order) serviceCenter.search(orderId, Order.class);
        if (order == null) {
            updateOrderStatusLock.unlock();

            return R.failed(null, "订单不存在");
        }

        try {
            updateOrderStatusLock.tryLock(60000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        order.setStatus(status);

        boolean flag = serviceCenter.update(order);

        updateOrderStatusLock.unlock();


        if (flag) {
            return R.ok(null, "订单状态修改成功");
        } else {
            return R.failed(null, "订单状态修改失败");
        }
    }

    //根据用户id查询订单
    @PostMapping("/getOrder/{offset}")
    @ApiOperation("根据用户id查询订单")
    public R getOrder(@ApiParam("已展示的数据条数") @PathVariable("offset") Integer offset,@RequestBody SearchOrderForm searchOrderForm,@RequestHeader("uid") String uid) {
        // 根据页码和每页数据量计算偏移量
//        long offset = (page - 1) * size;
//        searchOrderForm.put("limit",offset+" "+size);
        String searchcontent = searchOrderForm.getSearchContent();//商品描述
        //根据商品描述查询商品id
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("description",searchcontent);
        List<Product> productList = productService.list(queryWrapper);
        if (productList.size() == 0) {
            return R.failed(null, "查找不到任何订单");
        }


        List<String> productIdList = new ArrayList<>();

        for (Product product : productList) {
            productIdList.add(product.getProductId());
        }



//        Map<String, Object>  searchOrderMap = new HashMap<>();
//
//        searchOrderMap.put("limit",offset+" "+10);
//
//        List<ShowOrder> showOrderList =  orderService.getOrderListByUid(searchOrderMap,uid);

        List<ShowOrder> showOrderList =  orderService.getOrderListByMyUid(offset,productIdList,uid);
        if (showOrderList.size()==0){
            return R.failed(null,"查找不到任何相关订单");
        }
        return R.ok(showOrderList, "查询成功");
    }

    //根据卖家id查询订单
    @PostMapping("/getOrderBySellerId/{offset}")
    @ApiOperation("根据卖家id查询订单")
    public R getOrderBySellerId(@ApiParam("已展示的数据条数") @PathVariable("offset") Integer offset, @RequestBody SearchOrderForm searchOrderForm, @RequestHeader("uid") String sellerId) {
        // 根据页码和每页数据量计算偏移量
//        long offset = (page - 1) * size;
//        searchOrderForm.put("limit",offset+" "+size);
//        searchOrderForm.put("limit",offset+" "+10);

        String searchcontent = searchOrderForm.getSearchContent();//商品描述
        //根据商品描述查询商品id
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("description",searchcontent);
        List<Product> productList = productService.list(queryWrapper);
        if (productList.size() == 0) {
            return R.failed(null, "查找不到任何相关订单");
        }


        List<String> productIdList = new ArrayList<>();

        for (Product product : productList) {
            productIdList.add(product.getProductId());
        }

        List<ShowOrder> showOrderList =  orderService.getOrderListByTheSellerId(offset,productIdList,sellerId);
//        List<ShowOrder> showOrderList =  orderService.getOrderListBySellerId(searchOrderForm,sellerId);
        if (showOrderList.size()==0){
            return R.failed(null,"查找不到任何相关订单");
        }
        return R.ok(showOrderList, "查询成功");
    }

    //根据订单id删除订单
    @DeleteMapping("{orderId}")
    @ApiOperation("根据订单id删除订单")
    public R deleteOrder(@ApiParam("订单id") @PathVariable("orderId") String orderId) {

        RLock deleteOrderLock = redissonClient.getLock("deleteOrder"+orderId);

        try {
            deleteOrderLock.tryLock(60000,1500, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        boolean flag = serviceCenter.delete(orderId, Order.class);

        deleteOrderLock.unlock();
        if (flag) {
            return R.ok(null, "订单删除成功");
        } else {
            return R.failed(null, "订单删除失败");
        }
    }

    //支付订单
    @PostMapping("/payOrder/{orderId}")
    @ApiOperation("支付订单")
    public R payOrder(@ApiParam("订单id") @PathVariable("orderId") String orderId) {
       return orderService.payOrder(orderId);
    }

    //根据订单id查看订单信息
    @GetMapping("{orderId}")
    @ApiOperation("根据订单id查看订单信息")
    public R getOrderByOrderId(@ApiParam("订单id") @PathVariable("orderId") String orderId){
        Order order = (Order) serviceCenter.search(orderId, Order.class);
        if (order == null) {
            return R.failed(null, "订单不存在");
        }

        ShowOrder showOrder = new ShowOrder();
        showOrder.setOrderNo(order.getOrderNo());
        showOrder.setTotalPrice(order.getTotalPrice());
        showOrder.setOrderId(order.getOrderId());
        //商品信息
        String productId = order.getProductId();
        Product product = (Product) serviceCenter.selectMySql(productId, Product.class);
//            Product product = new Gson().fromJson(search.toString(), Product.class);
        showOrder.setProduct(product);
        //买家信息
        R user = userClient.getUserById(order.getUserId());
        Map<String, Object> data = (Map<String, Object>) user.getData();
        //收货地址
        String address = (String) data.get("address");
        //手机号
        String telephone = (String) data.get("telephone");
        //收货人
        String consignee = (String) data.get("consignee");

        showOrder.setAddress(address);
        showOrder.setTelephone(telephone);
        showOrder.setConsignee(consignee);

        //卖家信息
        R seller = userClient.getUserById(order.getSellerId());
        Map<String, Object> sellerData = (Map<String, Object>) seller.getData();
        String sellerNickName = (String) sellerData.get("username");
        String sellerAvatar = (String) sellerData.get("userImage");
        showOrder.setSellerAvatar(sellerAvatar);
        showOrder.setSellerNickName(sellerNickName);
        showOrder.setCreateTime(order.getCreateTime());
        return R.ok(showOrder,"查询订单信息成功！");
    }

}

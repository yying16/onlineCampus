package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Order;
import com.campus.trade.domain.Product;
import com.campus.trade.vo.ConfirmOrderForm;
import com.campus.trade.feign.UserClient;
import com.campus.trade.service.OrderService;
import com.campus.trade.service.ProductService;
import com.campus.trade.vo.ShowOrder;
import com.campus.trade.vo.ShowProduct;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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


    //查询下单前确认信息（用户和商品信息）
    @GetMapping("/confirm/{productId}")
    @ApiOperation("查询下单前确认信息（用户和商品信息）")
    public R confirmOrder(@ApiParam("商品id") @PathVariable("productId") String productId, @RequestHeader("uid") String uid) {
        //查询商品信息
//        Product product = productService.getById(productId);
        Product product = (Product) serviceCenter.search(productId, Product.class);

        if (product == null) {
            return R.failed(null, "商品不存在");
        }

        R user = userClient.getUserById(uid);
        //收货地址
        Map<String, Object> data = (Map<String, Object>) user.getData();
        String address = (String) data.get("address");
        //手机号
        String telephone = (String) data.get("telephone");
        //收货人
        String consignee = (String) data.get("consignee");

        ConfirmOrderForm confirmOrderForm = new ConfirmOrderForm();
        confirmOrderForm.setProduct(product);
        confirmOrderForm.setAddress(address);
        confirmOrderForm.setTelephone(telephone);
        confirmOrderForm.setConsignee(consignee);
        return R.ok(confirmOrderForm);
    }

    //展示生成的订单信息
    @PostMapping("/createOrder")
    @ApiOperation("展示生成的订单信息")
    public R createOrder(@RequestBody ConfirmOrderForm confirmOrderForm, @RequestHeader("uid") String uid) {
        ShowOrder showOrder = orderService.showOrder(uid, confirmOrderForm);
        if (showOrder == null) {
            return R.failed(null, "订单生成失败");
        }
        return R.ok(showOrder);
    }


    //修改订单状态
    @PostMapping("/updateOrderStatus/{orderId}/{status}")
    @ApiOperation("修改订单状态")
    public R updateOrderStatus(@PathVariable("orderId") String orderId,@PathVariable("status") Integer status) {

        Order order = (Order) serviceCenter.search(orderId, Order.class);
        order.setStatus(status);

        boolean flag = serviceCenter.update(order);

        if (flag) {
            return R.ok();
        } else {
            return R.failed(null, "订单状态修改失败");
        }
    }

    //根据用户id查询订单
    @GetMapping("/getOrder/{page}/{size}/{uid}")
    @ApiOperation("根据用户id查询订单")
    public R getOrder(@ApiParam("页码") @PathVariable("page") long page,@ApiParam("一页展示的数据条数") @PathVariable("size") long size,@RequestBody Map<String, Object> searchOrderForm,@PathVariable("uid") String uid) {
        // 根据页码和每页数据量计算偏移量
        long offset = (page - 1) * size;
        searchOrderForm.put("limit",offset+" "+size);
        List<ShowOrder> showOrderList =  orderService.getOrderListByUid(searchOrderForm,uid);
        return R.ok(showOrderList);
    }

    //根据卖家id查询订单
    @GetMapping("/getOrderBySellerId/{page}/{size}/{sellerId}")
    @ApiOperation("根据卖家id查询订单")
    public R getOrderBySellerId(@ApiParam("页码") @PathVariable("page") long page,@ApiParam("一页展示的数据条数") @PathVariable("size") long size,@RequestBody Map<String, Object> searchOrderForm,@PathVariable("sellerId") String sellerId) {
        // 根据页码和每页数据量计算偏移量
        long offset = (page - 1) * size;
        searchOrderForm.put("limit",offset+" "+size);
        List<ShowOrder> showOrderList =  orderService.getOrderListBySellerId(searchOrderForm,sellerId);
        return R.ok(showOrderList);
    }

    //根据订单id删除订单
    @DeleteMapping("{orderId}")
    @ApiOperation("根据订单id删除订单")
    public R deleteOrder(@ApiParam("订单id") @PathVariable("orderId") String orderId) {
        Order order = (Order) serviceCenter.search(orderId, Order.class);
        if (order == null) {
            return R.failed(null, "订单不存在");
        }
        boolean flag = serviceCenter.delete(order);
        if (flag) {
            return R.ok();
        } else {
            return R.failed(null, "订单删除失败");
        }
    }

}

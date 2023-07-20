package com.campus.trade.service.impl;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.dao.OrderDao;
import com.campus.trade.domain.Order;
import com.campus.trade.domain.Product;
import com.campus.trade.feign.UserClient;
import com.campus.trade.service.OrderService;
import com.campus.trade.vo.ConfirmOrderForm;
import com.campus.trade.vo.ShowOrder;
import com.campus.utils.OrderNoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.OrderUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
* @author xiaolin
* @description 针对表【t_order】的数据库操作Service实现
* @createDate 2023-07-09 11:38:21
*/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Order>
    implements OrderService{


    @Autowired
    private UserClient userClient;

    @Autowired
    private ServiceCenter serviceCenter;



    //查询订单信息
    @Override
    public ShowOrder showOrder(String uid, ConfirmOrderForm confirmOrderForm) {
        String productId = confirmOrderForm.getProduct().getProductId();
        BigDecimal totalPrice = confirmOrderForm.getProduct().getPrice();
        Order order = new Order();
        //生成订单号
        String orderNo = OrderNoUtil.getOrderNo();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setSellerId(confirmOrderForm.getProduct().getUserId());
        order.setProductId(productId);
        order.setTotalPrice(totalPrice);
        order.setStatus(0);//状态（0未支付)

        String insert = serviceCenter.insert(order);

//        boolean save = this.save(order);
        if(insert!=null){
            ShowOrder showOrder = new ShowOrder();
            showOrder.setOrderNo(orderNo);
            showOrder.setTotalPrice(totalPrice);
            showOrder.setConsignee(confirmOrderForm.getConsignee());
            showOrder.setAddress(confirmOrderForm.getAddress());
            showOrder.setTelephone(confirmOrderForm.getTelephone());
            showOrder.setProduct(confirmOrderForm.getProduct());
            //获取卖家昵称
            R user = userClient.getUserById(confirmOrderForm.getProduct().getUserId());
            Map<String, Object> data = (Map<String, Object>) user.getData();
            String nickName = (String) data.get("username");
            String avatar = (String) data.get("userImage");
            showOrder.setSellerAvatar(avatar);
            showOrder.setSellerNickName(nickName);
            showOrder.setCreateTime(order.getCreateTime());
            return showOrder;
        }
        return null;
    }

    //买入的订单
    @Override
    public List<ShowOrder> getOrderListByUid(Map<String, Object> searchOrderForm,String uid) {

//        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id",uid);
//        List<Order> orders = this.list(queryWrapper);

        searchOrderForm.put("userId",uid);
        List<Order> orders = serviceCenter.search(searchOrderForm, Order.class);

        if(orders==null){
            return null;
        }


        //封装数据
        List<ShowOrder> showOrders = getShowOderList(orders);

        return showOrders;
    }

    //封装数据
    public List<ShowOrder> getShowOderList(List<Order> orders){
        List<ShowOrder> showOrders = new ArrayList<>();
        //封装数据
        for (Order order:orders){
            ShowOrder showOrder = new ShowOrder();
            showOrder.setOrderNo(order.getOrderNo());
            showOrder.setTotalPrice(order.getTotalPrice());
            //商品信息
            String productId = order.getProductId();
            Object search = serviceCenter.search(productId, Product.class);
            Product product = new Gson().fromJson(search.toString(), Product.class);
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
            showOrders.add(showOrder);
        }
        return showOrders;
    }


    //卖出的订单
    @Override
    public List<ShowOrder> getOrderListBySellerId(Map<String, Object> searchOrderForm,String sellerId) {

        searchOrderForm.put("sellerId",sellerId);
        List<Order> orders = serviceCenter.search(searchOrderForm, Order.class);
//        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("seller_id",sellerId);
//        List<Order> orders = this.list(queryWrapper);
        if(orders==null){
            return null;
        }

        //封装数据
        List<ShowOrder> showOrders = getShowOderList(orders);
        return showOrders;
    }


}





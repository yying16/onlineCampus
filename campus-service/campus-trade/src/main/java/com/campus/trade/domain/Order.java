package com.campus.trade.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 *
 * @TableName t_order
 */
@TableName(value ="t_order")
@Data
public class Order implements Serializable {
    /**
     * 订单id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String orderId;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 下单用户id
     */
    private String userId;

    /**
     * 卖家id
     */
    private String sellerId;

    /**
     * 下单产品id
     */
    private String productId;



    /**
     * 总价
     */
    private BigDecimal totalPrice;

    /**
     * 状态
     */
    private Integer status; //状态（0未支付，1已支付，2已发货，3已收货）

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTime;

    /**
     * 逻辑删除
     */
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

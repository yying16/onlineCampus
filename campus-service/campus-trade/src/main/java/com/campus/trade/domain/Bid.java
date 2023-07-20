package com.campus.trade.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 *
 * @TableName t_bid
 */
@TableName(value ="t_bid")
@Data
public class Bid implements Serializable {
    /**
     * 竞价id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String bidId;

    /**
     * 商品id
     */
    private String productId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 价格
     */
    private BigDecimal price;

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

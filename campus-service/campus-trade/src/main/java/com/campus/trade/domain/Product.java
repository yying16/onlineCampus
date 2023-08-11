package com.campus.trade.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 *
 * @TableName t_product
 */
@TableName(value ="t_product")
@Data
public class Product implements Serializable {
    /**
     * 商品id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String productId;

    /**
     * 商品名
     */
    private String name;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 商家id
     */
    private String userId;

    /**
     * 分类id
     */
    private String categoryId;

    /**
     * 是否发布
     */
    private Integer isPublished;

    /**
     * 是否卖出
     */
    private Integer status; // 0:未卖出 1:已卖出

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

    /**
     * 图片列表（上传图片后返回的url）
     */
    @TableField(exist = false)
    private List<String> images; // 图片列表

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

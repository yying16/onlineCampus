package com.campus.trade.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 兼职收藏表
 * @TableName t_product_favorites
 */
@TableName(value ="t_product_favorites")
@Data
public class ProductFavorites implements Serializable {
    /**
     * 收藏记录编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String favoritesId;

    /**
     * 用户编号
     */
    private String userId;

    /**
     * 商品编号
     */
    private String productId;

    /**
     * 逻辑删除
     */
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    // 逻辑删除

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String updateTime;

    /**
     * 商品名
     */
    private String productName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

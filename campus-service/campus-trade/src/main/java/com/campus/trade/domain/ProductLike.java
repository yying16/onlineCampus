package com.campus.trade.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 兼职点赞表
 * @TableName t_product_like
 */
@TableName(value ="t_product_like")
@Data
public class ProductLike implements Serializable {
    /**
     * 点赞记录编号
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String likeId;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

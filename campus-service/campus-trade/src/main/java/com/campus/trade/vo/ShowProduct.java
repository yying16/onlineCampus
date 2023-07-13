package com.campus.trade.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.campus.trade.domain.Image;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @auther xiaolin
 * @create 2023/7/13 10:54
 */
@Data
public class ShowProduct {
    /**
     * 商品id
     */

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
    private Integer deleted;


    //用户名
    private String userName;

    //父分类名
    private String categoryName;

    //子分类名
    private String subCategoryName;



    //用户头像
    private String userAvatar;


    //商品图片列表
    private List<String> imageUrls;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

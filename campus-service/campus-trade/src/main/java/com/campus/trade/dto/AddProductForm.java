package com.campus.trade.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

/**
 * @auther xiaolin
 * @create 2023/7/9 12:28
 */

@Data
public class AddProductForm {


    /**
     * 商品名
     */
    @NotNull(message = "商品名不能为空")
    @NotBlank(message = "商品名不能为空")
    private String name;

    /**
     * 商品描述
     */
    @NotNull(message = "商品描述不能为空")
    @NotBlank(message = "商品描述不能为空")
    private String description;

    /**
     * 价格
     */
    @NotNull(message = "价格不能为空")
    @NotBlank(message = "价格不能为空")
    private BigDecimal price;

    /**
     * 商家id
     */
    @NotNull(message = "商家不能为空")
    @NotBlank(message = "商家不能为空")
    private String userId;

    /**
     * 分类id
     */
    @NotNull(message = "分类不能为空")
    @NotBlank(message = "分类不能为空")
    private String categoryId;


    /**
     * 商品图片列表（上传图片后返回的url）
     */
    @NotNull(message = "图片列表不能为空")
    @NotBlank(message = "图片列表不能为空")
    List<String> images; // 图片列表

    /**
     * 是否发布
     */
    @NotNull(message = "发布状态不能为空")
    @NotBlank(message = "发布状态不能为空")
    private Integer isPublished;
}

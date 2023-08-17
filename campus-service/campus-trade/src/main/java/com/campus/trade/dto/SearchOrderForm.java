package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/8/15 15:56
 */
@Data
public class SearchOrderForm {
    @NotNull(message = "查询内容不能为空")
    @NotBlank(message = "查询内容不能为空")
    String searchContent;//现在是商品名
}

package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/5 13:48
 */
@Data
public class AddBidForm {

    @NotBlank(message = "出价不能为空")
    @NotNull(message = "出价不能为空")
    private BigDecimal price;

    @NotBlank(message = "商品id不能为空")
    @NotNull(message = "商品id不能为空")
    private String productId;
}

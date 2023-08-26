package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/26 16:34
 */
@Data
public class GetProductListForm {

    @NotNull(message = "查询内容不能为空")
    @NotBlank(message = "查询内容不能为空")
    String searchContent;//现在是商品描述,前端輸入框的內容

    String categoryId;//商品分类id

    BigDecimal minPrice;//最低价格

    BigDecimal maxPrice;//最高价格

    String  time;//發佈時間

    String  sort;//其他排序方式
}

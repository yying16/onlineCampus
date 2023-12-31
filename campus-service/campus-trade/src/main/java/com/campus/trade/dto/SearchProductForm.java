package com.campus.trade.dto;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/15 15:35
 */
@Data
public class SearchProductForm {


    @NotNull(message = "查询内容不能为空")
    @NotBlank(message = "查询内容不能为空")
    String searchContent;//现在是商品描述,前端輸入框的內容




}

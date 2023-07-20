package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/7/20 16:12
 */
@Data
public class UpdateCategoryForm {

    @NotNull(message = "分类名不能为空")
    @NotBlank(message = "分类名不能为空")
    String name;


    @NotNull(message = "分类id不能为空")
    @NotBlank(message = "分类id不能为空")
    String categoryId;
}

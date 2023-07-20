package com.campus.trade.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/7/20 13:03
 */
@Data
public class AddCategoryForm {
    @NotNull(message = "分类名不能为空")
    @NotBlank(message = "分类名不能为空")
    String name;

    @NotNull(message = "父分类id不能为空")
    @NotBlank(message = "父分类id不能为空")
    String parentId;
}

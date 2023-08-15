package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/8/11 13:09
 */
@Data
public class AddCardForm {


    @NotNull(message = "卡密类型id不能为空")
    @NotBlank(message = "卡密类型id不能为空")
    String cardSMId; // 卡密类型id

    @NotNull(message = "生成数量不能为空")
    @NotBlank(message = "生成数量不能为空")
    Integer number; // 生成数量
}

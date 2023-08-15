package com.campus.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @auther xiaolin
 * @create 2023/8/11 13:19
 */

@Data
public class UpdateCardForm {


    @NotNull(message = "卡密不能为空")
    @NotBlank(message = "卡密不能为空")
    String cardKey;

    @NotNull(message = "卡密状态不能为空")
    @NotBlank(message = "卡密状态不能为空")
    Boolean status;

    @NotNull(message = "卡密类型不能为空")
    @NotBlank(message = "卡密类型不能为空")
    String cardsmid;
}

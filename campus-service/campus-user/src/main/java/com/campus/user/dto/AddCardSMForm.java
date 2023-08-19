package com.campus.user.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.models.auth.In;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:51
 */

@Data
public class AddCardSMForm {

    @NotNull(message = "卡密类型id不能为空")
    @NotBlank(message = "卡密类型id不能为空")
    String name; // 卡密类型名称

    @NotNull(message = "有效期不能为空")
    @NotBlank(message = "有效期不能为空")
    Integer validity; // 有效期

    @NotNull(message = "金额不能为空")
    @NotBlank(message = "金额不能为空")
    BigDecimal money; // 金额

    String virtualGoods; // 虚拟物品
}

package com.campus.parttime.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * author kakakaka
 */
@Data
public class JobOrderForm {
    private  String operationId; // 执行Id
    private String uid; // 用户id
    private String remark; // 兼职名称
    private BigDecimal money; // 收入或支出金额
}

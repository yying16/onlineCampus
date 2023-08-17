package com.campus.user.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/17 19:18
 */
@Data
public class RechargeRecord {

    private String id;

    private String userId;

    private BigDecimal money;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String createTime;

}

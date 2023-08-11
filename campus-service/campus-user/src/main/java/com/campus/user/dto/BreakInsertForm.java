package com.campus.user.dto;

import com.alibaba.nacos.shaded.org.checkerframework.checker.units.qual.A;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author kakakaka
 */

@Data

public class BreakInsertForm {
    String breakId; // 违规记录Id
    String breakerId; // 违规用户Id
    String breakText; // 违规内容
    String breakerAccount; // 用户账号
    String breakerName; // 用户名
}

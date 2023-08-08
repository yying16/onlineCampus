package com.campus.parttime.dto;

import lombok.Data;

/**
 * author kakakaka
 */
@Data
public class BreakInsertForm {
    String breakerId; // 违规用户Id
    String breakText; // 违规内容
    String breakerAccount; // 用户账号
    String breakerName; // 用户名
    Integer breakNum; // 违规次数
}

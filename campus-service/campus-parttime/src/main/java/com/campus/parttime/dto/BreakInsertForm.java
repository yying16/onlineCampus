package com.campus.parttime.dto;

import lombok.Data;

/**
 * author kakakaka
 */
@Data
public class BreakInsertForm {
    String breakerId; // 违规用户Id
    String breakText; // 违规内容
    String account; // 用户账号
    String userName; // 用户名
}

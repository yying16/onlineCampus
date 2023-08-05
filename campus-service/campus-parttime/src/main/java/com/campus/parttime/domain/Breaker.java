package com.campus.parttime.domain;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * author kakakaka
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_breaker")
public class Breaker {
    String breakerId; // 违规用户Id
    String breakText; // 违规内容
    String account; // 用户账号
    String userName; // 用户名

    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  //更新时间
}

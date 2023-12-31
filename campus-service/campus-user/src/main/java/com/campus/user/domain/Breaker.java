package com.campus.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.ASSIGN_ID)
    String breakId; // 违规记录Id
    String breakerId; // 违规用户Id
    String breakText; // 违规内容
    String breakerAccount; // 用户账号
    String breakerName; // 用户名

    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  //更新时间
}
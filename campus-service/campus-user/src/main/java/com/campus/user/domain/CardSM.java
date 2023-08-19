package com.campus.user.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @auther xiaolin
 * @create 2023/8/10 21:42
 */

@Data
@TableName("t_cardsm")
@AllArgsConstructor
@NoArgsConstructor
public class CardSM {

    @TableId(type = IdType.ASSIGN_ID)
    String id; // 卡密类型id

    String name; // 卡密类型名称

    Integer validity; // 有效期

    BigDecimal money; // 金额

    String virtualGoods; // 虚拟物品

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String createTime; // 创建时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String updateTime; // 更新时间

    @TableLogic(value = "false", delval = "true")
    Boolean deleted; // 逻辑删除
}

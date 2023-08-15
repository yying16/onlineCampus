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
 * @auther xiaolin
 * @create 2023/8/10 21:40
 */
@Data
@TableName("t_card")
@AllArgsConstructor
@NoArgsConstructor
public class Card {

    @TableId(type = IdType.ASSIGN_ID)
    String id; // 卡id

    String cardKey; // 卡号

    String uid; // 使用者的用户id

    Boolean status; // 状态

    String cardsmid; // 卡密类型id

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String time; // 使用时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String createTime; // 创建时间

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    String updateTime; // 更新时间

    @TableLogic(value = "false", delval = "true")
    Boolean deleted; // 逻辑删除


}

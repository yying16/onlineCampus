package com.campus.contact.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_reply")
@AllArgsConstructor
@NoArgsConstructor
public class Reply {
    @TableId(type = IdType.ASSIGN_ID)
    String replyId;
    String bottleId;
    String promulgatorId;
    String replyContent;
    Integer visits;
    Integer likeNum;
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;      // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;       // 更新时间
}

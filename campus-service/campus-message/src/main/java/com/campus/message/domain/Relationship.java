package com.campus.message.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@TableName("t_relationship")
@AllArgsConstructor
@NoArgsConstructor
public class Relationship {
    @TableId(type = IdType.ASSIGN_ID)
    String relationshipId;  // 关系id
    String sender;          // 发起关系申请者
    String receiver;        // 接收关系申请者
    Integer status;         //关系状态(0-正常关系，1-屏蔽,-1 -删除)
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;      // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;       // 更新时间
}

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
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_like")
public class Like {
    String likeId; // 点赞编号
    String userId; // 用户编号
    String jobId; // 兼职编号
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    // 逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  // 更新时间
}

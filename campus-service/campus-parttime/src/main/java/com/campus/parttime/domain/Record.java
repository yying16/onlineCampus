package com.campus.parttime.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
@TableName("t_job_record")
//兼职浏览记录表
public class Record {
    @TableId(type = IdType.ASSIGN_ID)
    String job_record_id; // 记录表编号
    String user_id; // 当前登录用户
    String job_id; // job主键
    Double score; // 比分
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String visit_time; // 浏览时间戳
}

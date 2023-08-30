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
@TableName("t_report")

public class Report {
    @TableId(type = IdType.ASSIGN_ID)
    String reportId; //举报记录Id
    String reporterId; //举报人的Id
    String reporterName; // 举报人的姓名
    String reportedId; // 被举报人id
    String reportedName; // 被举报人姓名
    String reportContent; // 举报详情
    Integer reportType; // 举报类型(0-其他，1-不当言论，2-侵犯版权，3-违规行为，4-内容低俗，5-不当广告，6-虚假信息）
    Integer reportStatus; // 举报状态（0-审核中，1-确认，2-驳回）
    String otherId; // 关联id
    String otherType; // 关联表名
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  //更新时间
}
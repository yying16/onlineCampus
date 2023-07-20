package com.campus.recruit.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_recruit")
public class Recruit {
    @TableId(type = IdType.ASSIGN_ID)
    String recruitId; // 主键
    String promulgator; // 发布者id
    String content; // 内容
    String photo; // 图片链接
    String requirement; // 招募要求(json)(key:小标题,value:格式)
    Integer status; // 发布状态(0-发布中，1-已结束,2-已关闭)
    Integer recruitNum; // 招募人数
    Integer deliverNum; //投递简历的人数
    String recruitDdl; //招募截至时间
    String recruitTel; //招募联系方式
    Integer visits; //访问次数
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;      // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;       // 更新时间

}

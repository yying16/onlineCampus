package com.campus.contact.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@TableName("t_bottle")
@AllArgsConstructor
@NoArgsConstructor
public class Bottle {
    @TableId(type = IdType.ASSIGN_ID)
    String bottleId; // 主键
    String promulgatorId; // 发布者id
    String content; // 漂流瓶内容
    Integer status; // 状态(0-正常，1-关闭，2-异常)
    Integer category; // 分类(0-其他,1-交友,2-表白，3-问答，4-搞笑，5-心灵鸡汤，6-吐槽，7-美食，8-生活，9-旅游，10-健康，11-文化，12-技能，13-科普，14-祝福)
    Integer replyNum; //回复数
    Integer visits; //浏览次数
    @TableField(exist = false)
    List<String> images; // 图片
    @TableLogic(value = "false", delval = "true")
    Boolean deleted;        //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;      // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;       // 更新时间

}

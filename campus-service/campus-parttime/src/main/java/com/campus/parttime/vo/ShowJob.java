package com.campus.parttime.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * author kakakaka
 */
@Data
public class ShowJob {
    String userId; // 用户id
    String userImage;   //用户头像
    String username;    //用户名(长度为2-12)
    Integer credit;     //信用值

    String jobId;       // 兼职编号
    String publisherId; // 发布者编号
    String jobTitle;    // 兼职职位名称
    BigDecimal salary;      // 兼职薪资
    Integer recruitNum;// 需招聘人数
    Integer workingDays; // 工作天数
    Integer term; // 兼职任期(0-短期，1-长期)
    Integer location; // 兼职所在校区(0-佛山校区，1-广州校区)
    String deadline;    // 截止时间
    String jobContent;  // 兼职详情
    List<String> images;  // 图片
    Integer status;     // 兼职职位状态(0-开启，1-关闭，2-招满，3-完成)
    Integer classification; // 兼职分类(0-代购，1-跑腿，2-学习，3-宣传，4-技术，5-家教，6-助理，7-其他)
    String createTime;  // 创建时间
    String updateTime;  // 更新时间

    Integer applyNum;  // 已申请人数
    Integer passedNum; // 已通过人数
    Integer finishNum; // 已完成人数

    @TableLogic(value = "false", delval = "true")
    Integer likeStatus;
    @TableLogic(value = "false", delval = "true")
    Integer favoritesStatus;
    @TableLogic(value = "false", delval = "true")
    Integer applyStatus;

    Integer visitNum; //访问次数
    Integer likeNum; // 点赞人数
    Integer favoritesNum; // 收藏人数
}

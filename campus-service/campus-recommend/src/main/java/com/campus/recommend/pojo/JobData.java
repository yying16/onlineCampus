package com.campus.recommend.pojo;

/**
 * job训练模型特征
 * 测试训练集的Y值为用户访问表的分数
 *
 * 用户信用度
 *
 * 用户性别
 * 用户年级
 *
 * */
public class JobData {

    String gender; //性别（0-女，1-男）
    String grade; // 年级
    Double credit; // 用户的信用值
    Double breakNum; // 用户违规次数
    Double entropy; // 金额熵（用户平均接单金额/兼职金额）
    Double term; // 兼职时长
    Double recruitNum;//需要招聘的人数
    Double classification;

    Double score; // 满意度

}

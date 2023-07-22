package com.campus.contact.domain;

import com.campus.common.util.TimeUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author yying
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "dynamic")
public class Dynamic implements Comparable<Dynamic>{
    @Id
    private String _id; // 主键
    @Indexed(unique = true)
    private String uuid; // 唯一id
    private String promulgatorId;//发布者账号(用户获取用户详情)
    private String promulgatorName;//发布者昵称(冗余数据)
    private String promulgatorImage; // 发布者头像(冗余数据)
    @Indexed
    private String content;//文案
    private List<String> photos = new ArrayList<>();//图片url集合
    @Indexed
    private String city;//城市
    private String address ; // 地理位置信息
    private List<String> label = new ArrayList<>(); // 标签
    private List<String> likeId = new ArrayList<>(); // 点赞用户id列表
    private List<String> likeName = new ArrayList<>(); // 点赞用户名称列表
    private List<String> targets = new ArrayList<>(); // 可查看列表（当status为部分好友可见有效）
    private Integer status = 0;//状态(0-公开/1-部分好友可见/2-私有)
    @DBRef
    private List<Comment> comments = new ArrayList<>(); //直接下级评论
    @Indexed
    private Boolean deleted = false;//删除标记
    @Indexed
    private String createTime; // 创建时间
    @Indexed
    private String updateTime;// 更新时间

    @Override
    public int compareTo(@NotNull Dynamic o) {
        long a = TimeUtil.getTimeStamp(this.createTime);
        long b = TimeUtil.getTimeStamp(o.createTime);
        return Long.compare(b,a);
    }
}

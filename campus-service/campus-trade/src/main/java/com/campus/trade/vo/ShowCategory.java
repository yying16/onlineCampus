package com.campus.trade.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.catalina.LifecycleState;

import java.io.Serializable;
import java.util.List;

/**
 * @auther xiaolin
 * @create 2023/7/13 14:22
 */
@Data
public class ShowCategory implements Serializable {
    /**
     * 分类id
     */
    private String categoryId;

    /**
     * 分类名
     */
    private String name;

    /**
     * 上级id
     */
    private String parentId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTime;

    /**
     * 逻辑删除
     */
    private Integer deleted;

    private List<ShowCategory> children;

    private static final long serialVersionUID = 1L;
}

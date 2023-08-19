package com.campus.trade.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 *
 * @TableName t_image
 */
@TableName(value ="t_image")
@Data
public class Image implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String imgId;

    /**
     *
     */
    private String imgUrl;

    /**
     *
     */
    private String otherId;

    /**
     *
     */
    private String otherType;

    /**
     *
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTime;

    /**
     *
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTime;

    /**
     *
     */
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

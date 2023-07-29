package com.campus.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("t_message")
public class Image implements Serializable {

    private String imgId;
    private String imgUrl;
    private String otherId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String createTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private String updateTime;
    @TableLogic(value = "false", delval = "true")
    private Boolean deleted;
}
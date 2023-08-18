package com.campus.user.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 *
 * @TableName t_details_change
 */
@TableName(value ="t_details_change")
@Data
public class DetailsChange implements Serializable {
    /**
     * 零钱明细id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 类型，充值收入还是支付支出
     */
    private Integer type;

    /**
     * 备注，零钱来源或用途
     */
    private String remark;

    /**
     * 收入或支出金额
     */
    private BigDecimal money;

    /**
     * 账号余额
     */
    private BigDecimal balance;

    private String avatar;

    @TableLogic(value = "false", delval = "true")
    Boolean deleted;    //逻辑删除
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String createTime;  //创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    String updateTime;  //更新时间

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}

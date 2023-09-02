package com.campus.parttime.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * author kakakaka
 */
@TableName(value ="t_details_change")
@Data
public class BalanceRecord {
        @TableId(type = IdType.ASSIGN_UUID)
        private String id; // 零钱明细id
        private String uid; // 用户id
        private Integer type; // 类型，充值收入还是支付支出
        private String remark; // 兼职名称（表明金额的支出和转入的来源）
        private BigDecimal money; // 收入或支出金额
        private BigDecimal balance; // 账号余额
        private String avatar;
        @TableLogic(value = "false", delval = "true")
        Boolean deleted;    //逻辑删除
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        String createTime;  //创建时间
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
        String updateTime;  //更新时间
}

package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.BalanceRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface BalanceRecordDao extends BaseMapper<BalanceRecord> {
    @Update("update t_user set balance=balance-#{money} where user_id=#{userId}")
    void payJob(String userId, BigDecimal money);

    @Select("select balance from t_user where user_id=#{userId}")
    BigDecimal searchBalance(String userId);

    @Update("update t_user set balance=balance+#{money} where user_id=#{userId}")
    void receiveJobPay(String userId, BigDecimal money);
}

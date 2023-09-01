package com.campus.parttime.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

@Mapper
public interface BalanceRecordDao {
    @Update("update t_user set balance=balance-#{money} where user_id=#{userId}")
    void payJob(String userId, BigDecimal money);

    @Select("select balance from t_user where user_id=#{userId}")
    BigDecimal searchBalance(String userId);

    @Update("update t_user set balance=balance+#{money} where user_id=#{userId}")
    void receiveJobPay(String userId, BigDecimal money);
}

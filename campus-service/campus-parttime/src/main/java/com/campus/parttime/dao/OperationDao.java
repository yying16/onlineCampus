package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Operation;
import com.campus.parttime.pojo.MonthlyStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface OperationDao extends BaseMapper<Operation> {
    @Select("select (select count(*) from t_operation where applicant_id=#{applicantId} AND status=3)/count(*) from t_operation where applicant_id=#{applicantId}")
    Double searchPersonalCompletionRate(String applicantId);

    @Select("SELECT v_cnt_operation.date,completed_cnt/v_cnt_operation.all_cnt rate from v_cnt_operation, v_completed_operation where v_cnt_operation.date = v_completed_operation.date and  v_completed_operation.date between date(#{begin}) and date(#{end})")
    List<MonthlyStatistics> searchPublicCompletionRate(@Param("begin")String begin, @Param("end")String end);
}

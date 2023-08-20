package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface LikeDao extends BaseMapper<Like> {
    @Select("select like_id from t_job_like where user_id=#{userId} and job_id=#{jobId} and deleted=0")
    String searchLikeIsExist(String userId,String jobId); // 根据用户和兼职id查询点赞记录是否存在

    @Update("update t_job_like set deleted=1 where job_id = #{job_id}")
    void deleteLikeByJobId(String jobId);


}

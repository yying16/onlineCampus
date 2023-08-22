package com.campus.parttime.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.parttime.domain.Favorites;
import com.campus.parttime.pojo.FavoritesList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface FavoritesDao extends BaseMapper<Favorites> {
    @Select("select favorites_id from t_job_favorites where user_id=#{userId} and job_id=#{jobId} and deleted=0")
    String searchFavoritesIsExist(String userId, String jobId);

    @Select("select favorites_id, user_id, job_id ,deleted, job_title from t_job_favorites where user_id= #{userId}")
    List<FavoritesList> SearchFavoritesByUserId(String userId);

    @Update("update t_job_favorites set deleted=1 where job_id = #{job_id}")
    void deleteFavoritesByJobId(String jobId);

    @Select("select user_id from t_job_favorites where job_id=#{jobId}")
    List <String> selectCollectorsByJobId(String jobId);



}

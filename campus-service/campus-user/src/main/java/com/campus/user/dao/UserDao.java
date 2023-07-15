package com.campus.user.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.user.domain.User;
import com.campus.user.dto.LoginForm;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserDao extends BaseMapper<User> {

    //获得用户对某本书的最后一次借阅记录
    @Select("select * from t_user where deleted = 0 and password = #{password} and (account = #{loginName} or telephone = #{loginName} or email = #{loginName})")
    User getUser(LoginForm loginForm);

    @Select("select * from t_user where telephone = #{telephone}")
    User getUserByTelephone(String telephone);

    @Select("select * from t_user where email = #{email}")
    User getUserByEmail(String email);
}

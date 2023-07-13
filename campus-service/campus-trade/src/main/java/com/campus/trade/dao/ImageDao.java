package com.campus.trade.dao;

import com.campus.trade.domain.Image;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author xiaolin
* @description 针对表【t_image】的数据库操作Mapper
* @createDate 2023-07-13 11:12:35
* @Entity com.campus.trade.domain.Image
*/
@Mapper
public interface ImageDao extends BaseMapper<Image> {

}





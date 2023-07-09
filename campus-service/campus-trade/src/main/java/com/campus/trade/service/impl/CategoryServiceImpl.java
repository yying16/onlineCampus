package com.campus.trade.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.trade.dao.CategoryDao;
import com.campus.trade.domain.Category;
import com.campus.trade.service.CategoryService;
import org.springframework.stereotype.Service;

/**
* @author xiaolin
* @description 针对表【t_category】的数据库操作Service实现
* @createDate 2023-07-09 11:38:21
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category>
    implements CategoryService{

}





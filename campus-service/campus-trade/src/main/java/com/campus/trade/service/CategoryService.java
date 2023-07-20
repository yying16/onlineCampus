package com.campus.trade.service;

import com.campus.trade.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.trade.vo.ShowCategory;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author xiaolin
* @description 针对表【t_category】的数据库操作Service
* @createDate 2023-07-09 11:38:21
*/
public interface CategoryService extends IService<Category> {

    List<ShowCategory> getCategoryList();

    void saveCategory(MultipartFile file, CategoryService categoryService);

    boolean deleteAllSubject(String subjectId);

    ShowCategory getCategory(String categoryId);
}

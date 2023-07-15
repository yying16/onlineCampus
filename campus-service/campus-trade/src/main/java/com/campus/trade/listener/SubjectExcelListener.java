package com.campus.trade.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.trade.domain.Category;
import com.campus.trade.domain.excel.CategoryData;
import com.campus.trade.service.CategoryService;

/**
 * @auther xiaolin
 * @creatr 2023/5/14 16:19
 */
public class SubjectExcelListener extends AnalysisEventListener<CategoryData> {

    //因为监听器不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
    //不能用mp实现数据库操作

    public CategoryService categoryService;

    public SubjectExcelListener(){}


    public SubjectExcelListener(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    //读取excel内容，一行一行进行读取
    @Override
    public void invoke(CategoryData subjectData, AnalysisContext analysisContext) {
        if (subjectData==null){
            throw new RuntimeException("文件数据为空");
        }

        //一行一行读取，每次读取有两个值，第一个值是一级分类，第二个值是二级分类
        //判断一级分类是否重复
        Category existOneSubject = this.existOneSubject(categoryService, subjectData.getOneSubjectName());
        if (existOneSubject==null) {//没有相同的一级分类，可以进行添加
            existOneSubject = new Category();
            existOneSubject.setParentId("0");
            existOneSubject.setName(subjectData.getOneSubjectName());;
            categoryService.save(existOneSubject);
        }

        String pid = existOneSubject.getCategoryId();

        //添加二级分类
        //判断二级分类是否重复
        Category existTwoSubject = this.existTwoSubject(categoryService, subjectData.getTwoSubjectName(), pid);
        if (existTwoSubject==null) {//没有相同的一级分类，可以进行添加
            existTwoSubject = new Category();
            existTwoSubject.setParentId(pid);
            existTwoSubject.setName(subjectData.getTwoSubjectName());;
            categoryService.save(existTwoSubject);
        }
    }


    //判断一级分类不能重复添加
    private Category existOneSubject(CategoryService subjectService,String name){
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        wrapper.eq("parent_id",0);
        Category oneSubject = subjectService.getOne(wrapper);
        return oneSubject;
    }


    //判断二级分类不能重复添加
    private Category existTwoSubject(CategoryService subjectService,String name,String pid){
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("name",name);
        wrapper.eq("parent_id",pid);
        Category twoSubject = subjectService.getOne(wrapper);
        return twoSubject;
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }
}

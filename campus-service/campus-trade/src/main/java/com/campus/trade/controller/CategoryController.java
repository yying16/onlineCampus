package com.campus.trade.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.trade.domain.Category;
import com.campus.trade.dto.AddCategoryForm;
import com.campus.trade.service.CategoryService;
import com.campus.trade.vo.ShowCategory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.Subject;
import java.util.List;

/**
 * @auther xiaolin
 * @create 2023/7/9 14:30
 */
@RestController
@RequestMapping("/category")
@Api("分类管理")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ServiceCenter serviceCenter;

    //根据分类id查询分类信息
    @GetMapping("{categoryId}")
    @ApiOperation("根据分类id查询分类信息")
    public R getCategoryById(@ApiParam("分类id") @PathVariable("categoryId") String categoryId){

//        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("category_id",categoryId);
//        Category category = categoryService.getOne(queryWrapper);
        ShowCategory showCategory = categoryService.getCategory(categoryId);
        if (showCategory==null){
            return R.failed(null,"分类不存在");
        }
        return R.ok(showCategory,"查询分类信息成功");
    }


    //分层查询查询所有分类信息（一级分类，二级分类）
    @GetMapping("/list")
    @ApiOperation("查询所有分类信息")
    public R getCategoryList(){
        List<ShowCategory> categoryList =  categoryService.getCategoryList();
        return R.ok(categoryList,"查询分类信息成功");
    }


    //批量添加分类信息
    // 添加课程分类
    // 获取上传过来的文件，把文件内容读取出来
    @ApiOperation(value = "Excel批量导入")
    @PostMapping("/addCategory")
    public R addCategory(MultipartFile file){
        //上传过来的excel文件
        categoryService.saveCategory(file,categoryService);
        return R.ok(null,"批量导入分类成功");
    }


    //一个方法实现添加一级分类和二级分类
    @PostMapping("/addSubject")
    @ApiOperation("添加一级分类或二级分类")
    public R addTwoLevel(@RequestBody AddCategoryForm addCategoryForm){
        Category subject = new Category();
        BeanUtils.copyProperties(addCategoryForm,subject);
        String insert = serviceCenter.insert(subject);
//        boolean save = categoryService.save(subject);
        if (insert!=null){
            return R.ok(null,"添加分类成功");
        }else {
            return R.failed(null,"添加分类失败");
        }
    }


    //删除二级分类
    @DeleteMapping("/deleteSubject/{subjectId}")
    @ApiOperation("删除二级分类")
    public R deleteSubject(@PathVariable String subjectId){
//        boolean b = categoryService.removeById(subjectId);
        boolean b = serviceCenter.delete(subjectId, Category.class);

        if (b){
            return R.ok(null,"删除二级分类成功");
        }else {
            return R.failed(null,"删除二级分类失败");
        }
    }


    //删除一级分类及其下面的二级分类
    @DeleteMapping("/deleteAllSubject/{subjectId}")
    @ApiOperation("删除一级分类及其下面的二级分类")
    public R deleteSubjectAll(@PathVariable String subjectId){
        boolean b = categoryService.deleteAllSubject(subjectId);
        if (b){
            return R.ok(null,"删除分类成功");
        }else {
            return R.failed(null,"删除分类失败");
        }
    }


    //根据id修改分类
    @PutMapping("/updateSubject")
    @ApiOperation("根据id修改分类")
    public R updateSubject(@RequestBody Category subject){
        boolean b = serviceCenter.update(subject);
//        boolean b = categoryService.updateById(subject);
        if (b){
            return R.ok(null,"修改分类成功");
        }else {
            return R.failed(null,"修改分类失败");
        }
    }



}

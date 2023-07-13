package com.campus.trade.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.service.ServiceCenter;
import com.campus.trade.dao.CategoryDao;
import com.campus.trade.domain.Category;
import com.campus.trade.domain.excel.CategoryData;
import com.campus.trade.listener.SubjectExcelListener;
import com.campus.trade.service.CategoryService;
import com.campus.trade.vo.ShowCategory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.security.auth.Subject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
* @author xiaolin
* @description 针对表【t_category】的数据库操作Service实现
* @createDate 2023-07-09 11:38:21
*/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category>
    implements CategoryService{


    @Autowired
    private ServiceCenter serviceCenter;
    @Override
    public List<ShowCategory> getCategoryList() {
        //先查询出所有分类
        List<Category> categoryList = baseMapper.selectList(null);

        List<ShowCategory> treeNodes = new ArrayList<>();
        for (Category category : categoryList) {
            ShowCategory showCategory = new ShowCategory();
            BeanUtils.copyProperties(category,showCategory);
            treeNodes.add(showCategory);
        }

        List<ShowCategory> trees = new ArrayList<>();

        for (ShowCategory treeNode : treeNodes) {
            //如果是一级分类
            if(treeNode.getParentId().equals("0")){
                trees.add(findChildren(treeNode,treeNodes));
            }
        }
        return trees;
    }

    @Override
    public void saveCategory(MultipartFile file, CategoryService categoryService) {
        try {
            InputStream inputStream = file.getInputStream();
            EasyExcel.read(inputStream, CategoryData.class,new SubjectExcelListener(categoryService))
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //删除一级分类及其下面的所有二级分类
    @Override
    public boolean deleteAllSubject(String subjectId) {

        //1.根据id查询是否有子节点
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id",subjectId);

        //删除二级分类
        baseMapper.selectList(wrapper).forEach(category -> {
            boolean delete = serviceCenter.delete(category);
            if(!delete){
                throw new RuntimeException("删除失败");
            }
        });

        //删除一级分类
//        int i = baseMapper.deleteById(subjectId);
        Category search = (Category) serviceCenter.search(subjectId, Category.class);
        boolean delete = serviceCenter.delete(search);
        return delete;
    }

    private ShowCategory findChildren(ShowCategory treeNode, List<ShowCategory> treeNodes) {
        treeNode.setChildren(new ArrayList<ShowCategory>());
        for (ShowCategory it : treeNodes) {
            if(treeNode.getCategoryId().equals(it.getParentId())){
                if(treeNode.getChildren() == null){
                    treeNode.setChildren(new ArrayList<ShowCategory>());
                }
                treeNode.getChildren().add(findChildren(it,treeNodes));
            }
        }
        return treeNode;
    }
}





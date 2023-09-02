package com.campus.recruit.controller;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.recruit.domain.Recruit;
import com.campus.recruit.dto.RecruitInsertForm;
import com.campus.recruit.dto.RecruitUpdateForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static com.campus.recruit.constant.RecruitStatus.*;

@RestController
@RequestMapping("/recruit")
@Api("招募模块接口")
public class RecruitController {

    @Autowired
    ServiceCenter serviceCenter;

    @ApiOperation("发布招募")
    @PostMapping("/issueRecruit")
    public R issueRecruit(@RequestBody RecruitInsertForm form) {
        Recruit recruit = FormTemplate.analyzeTemplate(form, Recruit.class);
        assert recruit != null;
        recruit.setStatus(RELEASING.code); // 初始化状态为已发布
        String id = serviceCenter.insert(recruit); // 主页项直接调用套件存储
        if (id != null) {
            return R.ok(id);
        }
        return R.failed();
    }

    @ApiOperation("修改招募")
    @PostMapping("/updateRecruit")
    public R updateRecruit(@RequestBody RecruitUpdateForm form) {
        Recruit recruit = FormTemplate.analyzeTemplate(form, Recruit.class);
        assert recruit != null;
        if (serviceCenter.update(recruit)) {// 主页项直接调用套件存储
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("删除招募")
    @GetMapping("/deleteRecruit")
    public R deleteRecruit(@RequestParam("recruitId") String recruitId) {
        if (serviceCenter.delete(recruitId, Recruit.class)) {// 主页项直接调用套件存储
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("查看招募详情")
    @GetMapping("/getRecruitDetail")
    public R getRecruitDetail(@RequestParam("recruitId") String recruitId) {
        Object recruit = serviceCenter.search(recruitId, Recruit.class);
        if (recruit != null) {
            return R.ok(recruit);
        }
        return R.failed();
    }

    @ApiOperation("结束招募")
    @GetMapping("/finishRecruit")
    public R finishRecruit(@RequestParam("recruitId") String recruitId) {
        Recruit recruit = new Recruit();
        recruit.setRecruitId(recruitId);
        recruit.setStatus(FINISHED.code);
        if (serviceCenter.update(recruit)) {
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("条件查询招募")
    @PostMapping("/searchRecruit")
    public R searchRecruit(@RequestBody Map condition) {
        List search = serviceCenter.search(condition, Recruit.class);
        if (search != null) {
            return R.ok(search);
        }
        return R.failed();
    }

    @ApiOperation("招募首页懒加载")
    @GetMapping("/lazyLoading")
    public R lazyLoading(@RequestParam("num") Integer num) {
        List<Recruit> recruits = serviceCenter.loadData(num, Recruit.class);
        if (recruits != null) {
            return R.ok(recruits);
        }
        return R.failed();
    }

    @ApiOperation("招募访问量+1")
    @GetMapping("/incrementVisitNum")
    public R incrementVisitNum(@RequestParam("recruitId") String recruitId){
        if(serviceCenter.increment(recruitId,Recruit.class,true,"visits")){
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("招募投递人数+1,访问量+1")
    @GetMapping("/incrementDeliverNum")
    public R incrementRecruitNum(@RequestParam("recruitId") String recruitId){
        if(serviceCenter.increment(recruitId,Recruit.class,true,"visits","deliverNum")){
            return R.ok();
        }
        return R.failed();
    }
}
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

import static com.campus.recruit.constant.RecruitStatus.RELEASING;

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
            return R.ok();
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
    public R deleteRecruit(@Param("recruitId") String recruitId) {
        if (serviceCenter.delete(recruitId, Recruit.class)) {// 主页项直接调用套件存储
            return R.ok();
        }
        return R.failed();
    }


}

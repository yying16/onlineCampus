package com.campus.parttime.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.parttime.domain.Apply;
import com.campus.parttime.domain.Job;
import com.campus.parttime.domain.Operation;
import com.campus.parttime.dto.ApplyStatusUpdateForm;
import com.campus.parttime.dto.JobInsertForm;
import com.campus.parttime.dto.JobUpdateForm;
import com.campus.parttime.dto.OperationInserForm;
import com.campus.parttime.vo.JobStatusUpdateForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.campus.parttime.constant.ApplyStatus.APPLIED;
import static com.campus.parttime.constant.ApplyStatus.PASSED;
import static com.campus.parttime.constant.JobStatus.*;
import static com.campus.parttime.constant.OperationStatus.*;

/**
 * author kakakaka
 */

/*
 *   updateMySql:用于更新非首页信息到数据库
 * */

@RestController
@RequestMapping("/parttime")
@Slf4j
@Api("兼职模块接口")
public class ParttimeController {

    @Autowired
    ServiceCenter serviceCenter;

    /**
     * （兼职发起者）用户自定义发布
     */
    @ApiOperation("发布兼职")
    @PostMapping("/addJob")
    public R addJob(@RequestBody JobInsertForm form) {
        Job job = FormTemplate.analyzeTemplate(form, Job.class);
        assert job != null;
        job.setStatus(OPEN.code); // 初始化状态为已发布
        String id = serviceCenter.insert(job); // 主页项直接调用套件存储
        if (id != null) {
            return R.ok(id);
        }
        return R.failed();
    }

    /**
     * （兼职发起者）用户自定义修改(包括了兼职关闭)
     */
    @ApiOperation("修改兼职信息")
    @PostMapping("/updateJobInfo")
    public R updateRecruit(@RequestBody JobUpdateForm form) {
        Job job = FormTemplate.analyzeTemplate(form, Job.class);
        assert job != null;
        if (serviceCenter.update(job)) {// 调用套件
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("删除兼职")
    @GetMapping("/deleteJob")
    public R deleteRecruit(@RequestParam("jobId") String jobId) {
        if (serviceCenter.delete(jobId, Job.class)) {// 调用套件
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("提交兼职申请")
    @GetMapping("/addJobApply")
    public R addJobApply(@RequestHeader("uid") String applicantId, @RequestParam("jobId") String jobId) {
        Job job = (Job) serviceCenter.search(jobId, Job.class);
        if(job.getStatus().equals(CLOSE.code)){
            log.info("兼职已关闭，提交失败");
            return R.failed(null,"兼职已关闭，提交失败");
        }
        //创建兼职申请记录
        Apply apply = new Apply();
        apply.setApplicantId(applicantId);
        apply.setJobId(jobId);
        apply.setStatus(APPLIED.code); // 初始化状态为已申请
        //雪花算法为该申请记录生成主键Id
        apply.setApplicationId(IdWorker.getIdStr(apply));

        //将该记录插入数据库中
        if (serviceCenter.insertMySql(apply)){//记录插入成功
            log.info("兼职申请插入数据库成功！");//打印日志
            //修改Job表兼职申请人数
            job.setApplyNum(job.getApplyNum() + 1);
            //将修改后的兼职记录修改到数据库中
            if (serviceCenter.updateMySql(job)) { // 插入数据库
                log.info("兼职记录修改失败");
                return R.ok(null,"提交成功");
            }
            return R.failed(null,"提交失败，请重试");
        }
        return R.failed(null,"提交失败，请重试");
    }

    @ApiOperation("删除兼职申请记录")
    @GetMapping("/deleteApply")
    public R deleteApply(@RequestParam("applicationId") String applicationId) {
        Apply apply = (Apply) serviceCenter.selectMySql(applicationId, Apply.class);
        assert apply != null;
        if(apply.getStatus().equals(PASSED.code)){
            log.info("申请已通过，删除失败");
            return R.failed(null,"申请已通过，删除失败");
        }
        if(serviceCenter.deleteMySql(Apply.class,applicationId)){
            log.info("兼职申请记录删除成功");
            Job job = (Job) serviceCenter.selectMySql(apply.getJobId(),Job.class);
            job.setApplyNum(job.getApplyNum()-1);
            if (serviceCenter.updateMySql(job)) {// 调用套件
                log.info("兼职申请人数修改成功");
                return R.ok();
            }
        }

        return R.failed();
    }

    @ApiOperation("兼职申请通过")
    @GetMapping("/passApply")
    @Transactional
    public R passApply(@RequestParam("applicationId") String applicationId) {
        Apply apply = (Apply) serviceCenter.selectMySql(applicationId, Apply.class);
        //申请前判断当前申请是否删除
        if(apply.getDeleted()){
            log.info("兼职记录已删除，兼职通过失败");
            return R.failed("兼职记录已删除");
        }
        apply.setStatus(PASSED.code);
        Job job = FormTemplate.analyzeTemplate(serviceCenter.selectMySql(apply.getJobId(), Job.class), Job.class);
        job.setPassedNum(job.getPassedNum() + 1);
        if (job.getPassedNum().equals(job.getRecruitNum())) {
            job.setStatus(FULL.code);
        }
        if (serviceCenter.updateMySql(apply) && serviceCenter.updateMySql(job)) { // 申请和兼职数据更新成功
            Operation operation = new Operation();
            operation.setOperationId(IdWorker.getIdStr(operation));
            operation.setApplicantId(apply.getApplicantId());
            operation.setJobId(apply.getJobId());
            operation.setPublisherId(job.getPublisherId());
            if (serviceCenter.insertMySql(operation)) {
                return R.ok();
            }
            log.info("兼职申请通过用例异常");
            return R.failed(null, "数据更新异常");
        }
        log.info("兼职申请通过用例异常");
        return R.failed(null, "数据更新异常");
    }

    /**
     * 修改申请状态
     */
    @ApiOperation("修改申请状态")
    @GetMapping("/updateApplyStatus")
    public R updateJobStatus(@RequestBody ApplyStatusUpdateForm form) {
        Apply apply = FormTemplate.analyzeTemplate(form, Apply.class);
        assert apply != null;
        if (serviceCenter.update(apply)) {// 调用套件
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("新增执行订单记录")
    @PostMapping("/addJobOperation")
    public R addJobOperation(@RequestBody OperationInserForm form) {
        Operation operation = FormTemplate.analyzeTemplate(form, Operation.class);
        assert operation != null;
        operation.setStatus(ACTIVE.code); // 初始化状态为已申请
        String id = serviceCenter.insert(operation); // 调用套件
        if (id != null) {
            return R.ok(id);
        }
        return R.failed();
    }

    @ApiOperation("删除兼职操作")
    @GetMapping("/deleteJobOperation")
    public R deleteJobOperation(@RequestParam("operationId") String OperationId) {
        if (serviceCenter.delete(OperationId, Operation.class)) {// 调用套件
            return R.ok();
        }
        return R.failed();
    }

    @ApiOperation("兼职订单反馈提交")
    @GetMapping("/updateJobFeedback")
    public R updateJobStatus(@RequestParam("operationId") String operationId, @RequestParam("postId") String postId, @RequestParam("feedback") String feedback) {
        Operation operation = FormTemplate.analyzeTemplate(serviceCenter.selectMySql(operationId, Operation.class), Operation.class);
        if (operation == null) {
            return R.failed();
        }
        if (feedback != null) {
            if (postId.equals(operation.getPublisherId())) {
                operation.setFeedback_from_publisher_to_applicant(feedback);
            } else operation.setFeedback_from_applicant_to_publisher(feedback);
            if (serviceCenter.update(operation)) {// 调用套件
                log.info("更新成功");
                return R.ok();
            } else return R.failed();
        } else return R.failed();
    }
}

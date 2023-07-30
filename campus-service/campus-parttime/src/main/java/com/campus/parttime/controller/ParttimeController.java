package com.campus.parttime.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.parttime.dao.ApplyDao;
import com.campus.parttime.domain.Apply;
import com.campus.parttime.domain.Job;
import com.campus.parttime.domain.Operation;
import com.campus.parttime.dto.*;
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

    @Autowired
    ApplyDao applyDao;

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
        serviceCenter.insertImage(form.getImage(),id,Job.class);
        if (id != null) {
            return R.ok(id);
        }
        return R.failed();
    }

    /**
     * 普通更新
     * 若applyNum==0 可以直接更新Job信息
     * 若applyNum>0&&passedNum==0,返回强制更新确认信息给前端
     * 若passedNum>0,则返回无法更新
     */
    @ApiOperation("普通更新兼职信息")
    @PostMapping("/updateJobInfo")
    public R updateJob(@RequestBody JobUpdateForm form) {
        Job job = FormTemplate.analyzeTemplate(form, Job.class);
        assert job != null;
        if(job.getApplyNum().equals(0)) {//若applyNum==0，直接更新Job信息
            if (serviceCenter.update(job)) {// 调用套件
                return R.ok();
            }
            return R.failed();
        }
        return R.failed("是否强制更新信息？");
    }

    /**
     * 强制更新
     * 若applyNum>0&&passedNum==0,进行强制更新
     * 删除：与当前job绑定的所有apply记录
     * 修改：applyNum=0;
     * 更新：job数据（update到缓存和数据库）
     * 后端调用message模块的sendPromptInformation方法发送提示信息给申请者
     * 返回：vo对象
     */
    @ApiOperation("强制更新兼职信息")//待修改
    @PostMapping("/forceUpdateJobInfo")
    public R forceUpdateJob(@RequestBody JobUpdateForm form) {
        Job job = FormTemplate.analyzeTemplate(form, Job.class);
        assert job != null;
        Job sqlJob = (Job)serviceCenter.selectMySql(job.getJobId(),Job.class);
        if(sqlJob.getApplyNum()>0 && sqlJob.getPassedNum().equals(0)){
            //逻辑删除与当前job绑定的所有apply记录
            applyDao.deleteApplyByJobId(job.getJobId());
            //修改applyNum=0
            job.setApplyNum(0);
            //更新job数据（update到缓存和数据库）
            if (serviceCenter.update(job)) {
                //后端调用message模块的sendPromptInformation方法发送提示信息给申请者

                return R.ok();
            }
            return R.failed(null,"强制更新兼职信息失败");
        }
        return R.failed();
    }

    /**
     * 删除兼职
     * 兼职状态已完成或者已关闭
     */
    @ApiOperation("删除兼职")
    @GetMapping("/deleteJob")
    public R deleteJob(@RequestParam("jobId") String jobId) {
        Job job = (Job)serviceCenter.search(jobId,Job.class);
        if(job.getStatus().equals(FINISH.code) && job.getStatus().equals(CLOSE.code)){
            if (serviceCenter.delete(jobId, Job.class)) {// 调用套件
                return R.ok();
            }
            return R.failed("兼职删除失败");
        }
        return R.failed("兼职状态设置不为关闭或未完成");
    }

    /**
     * 提交兼职申请:
     * 若兼职状态为关闭或者招满,无法提交兼职申请;
     * 创建兼职申请记录:初始化基本信息,修改初始状态为已申请,生成主键Id;
     * 修改Job表兼职申请人数;
     * 将该记录插入数据库中
     */
    @ApiOperation("提交兼职申请")
    @GetMapping("/addJobApply")
    public R addJobApply(@RequestHeader("uid") String applicantId, @RequestParam("jobId") String jobId) {
        Job job = (Job) serviceCenter.search(jobId, Job.class);
        // 若当前兼职状态为关闭或者招满，兼职申请提交失败
        if(job.getStatus().equals(CLOSE.code)){
            log.info("兼职已关闭，提交失败");
            return R.failed(null,"兼职已关闭，提交失败");
        }
        if(job.getStatus().equals(FULL.code)){
            log.info("兼职已招满，提交失败");
            return R.failed(null,"兼职已招满，提交失败");
        }

        // 创建兼职申请记录
        Apply apply = new Apply();
        apply.setJobId(jobId);
        apply.setApplicantId(applicantId);
        apply.setStatus(APPLIED.code); // 初始化状态为已申请

        // 雪花算法为该申请记录生成主键Id
        apply.setApplicationId(IdWorker.getIdStr(apply));

        //将该记录插入数据库中
        if (serviceCenter.insertMySql(apply)){ // 记录插入成功
            log.info("兼职申请插入数据库成功!"); // 打印日志
            //修改Job表兼职申请人数
            job.setApplyNum(job.getApplyNum() + 1);
            //将修改后的兼职记录修改到数据库中
            if (serviceCenter.updateMySql(job)) { // 插入数据库
                log.info("兼职记录修改失败");
                return R.ok(null,"提交成功");
            }
            return R.failed(null,"提交失败,请重试");
        }
        return R.failed(null,"提交失败,请重试");
    }

    /**
     * 删除兼职申请记录:
     * 若兼职申请状态为已通过,无法删除兼职申请记录;
     * 删除数据库中的兼职申请记录;
     * 修改Job表的申请人数（申请人数-1）
     * 将修改后的Job对象插入数据库中
     */
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

    /**
     * 通过兼职申请:
     * 若兼职申请记录为已删除,无法通过兼职申请;
     * 若当前兼职状态为招满，无法通过兼职申请;
     * 修改当前apply记录的状态为已通过;
     * 修改当前job记录的获得通过人数（passedNum+1）
     * 若通过该申请后，兼职招满了，就设置当前的兼职状态为招满
     * 将修改后的apply和job记录更新到数据库中
     * 若更新成功，新增执行订单operation记录，初始化operation的初始化信息，并插入数据库中
     */
    @ApiOperation("通过兼职申请")
    @GetMapping("/passApply")
    @Transactional
    public R passApply(@RequestParam("applicationId") String applicationId) {
        Apply apply = (Apply) serviceCenter.selectMySql(applicationId, Apply.class);
        //申请前判断当前申请是否删除
        if(apply.getDeleted()){
            log.info("兼职记录已删除，无法通过该兼职");
            return R.failed(null,"兼职记录已删除，无法通过该兼职");
        }
        Job job = FormTemplate.analyzeTemplate(serviceCenter.selectMySql(apply.getJobId(), Job.class), Job.class);
        if(job.getStatus().equals(FULL.code)){
            return R.failed(null,"当前兼职已招满，无法通过该兼职");
        }
        apply.setStatus(PASSED.code);
        job.setPassedNum(job.getPassedNum() + 1);
        if (job.getPassedNum().equals(job.getRecruitNum())) {
            job.setStatus(FULL.code);
        }
        if (serviceCenter.updateMySql(apply) && serviceCenter.update(job)) { // 申请和兼职数据更新成功
            Operation operation = new Operation();
            operation.setStatus(ACTIVE.code); // 初始化状态为进行中
            operation.setOperationId(IdWorker.getIdStr(operation));
            operation.setApplicantId(apply.getApplicantId());
            operation.setJobId(apply.getJobId());
            operation.setPublisherId(job.getPublisherId());
            // 雪花算法为该申请记录生成主键Id
            operation.setOperationId(IdWorker.getIdStr(operation));
            if (serviceCenter.insertMySql(operation)) {
                return R.ok();
            }
            else return R.failed(null,"兼职执行记录生成异常");
        }
        log.info("数据更新异常，请重试!");
        return R.failed(null, "数据更新异常，请重试!");
    }

    /**
     * 拒绝兼职申请：
     * 根据表单中的applicationId查找数据库对应的申请记录。若当前记录的申请状态为已通过，无法拒绝该兼职申请
     * 直接更新数据库
     */
    @ApiOperation("拒绝兼职申请")
    @GetMapping("/refuseApply")
    public R refuseApply(@RequestBody ApplyStatusUpdateForm form) {
        Apply apply = FormTemplate.analyzeTemplate(form, Apply.class);
        assert apply != null;
        Apply applySql = (Apply) serviceCenter.selectMySql(apply.getApplicationId(),Apply.class);
        if(applySql.getStatus().equals(PASSED.code)){
            return R.failed(null,"申请已通过，拒绝失败");
        }
        if (serviceCenter.updateMySql(apply)) {// 调用套件
            return R.ok();
        }
        return R.failed(null,"数据更新异常，请重试!");
    }

    /**
     * 修改兼职操作状态：
     * 根据发起修改的ID进行判断：
     * （无权）若当前Id为兼职发布者，则无权修改执行订单状态为完成；若当前Id为兼职执行者，则无权修改执行订单状态为确认完成；若为其他Id，则无权进行所有状态的修改
     * （可修改）若当前Id为兼职发布者，可以将执行订单状态直接修改为确认完成；若当前Id为兼职执行者，可以将执行订单状态直接修改为已完成；（直接更新数据库）
     * （其他处理）若当前Id为兼职执行者或者兼职发布者，想修改执行订单状态为取消,系统会提示取消需要联系客服处理。
     */
    @ApiOperation("修改兼职操作状态")
    @GetMapping("/updateOperationStatus")
    public R updateOperationStatus(@RequestHeader("uid") String posterId,@RequestBody OperationStatusUpdateForm form) {
        Operation operation = FormTemplate.analyzeTemplate(form,Operation.class);
        assert operation != null;
        if(posterId.equals(operation.getApplicantId()) && posterId.equals(operation.getPublisherId())){//判断当前Id是否为兼职发布者或执行者Id
            if(posterId.equals(operation.getApplicantId()) && operation.getStatus().equals(CONFIRM.code)) {//若当前Id为执行者并且需要修改状态为确认完成
                return R.failed(null, "您没有权限确认订单完成");
            }
            if(posterId.equals(operation.getPublisherId()) && operation.getStatus().equals(COMPLETED.code)){//若当前Id为发布者并且需要修改状态为完成
                return R.failed(null, "您没有权限完成订单");
            }
            if(operation.getStatus().equals(CANCEL.code)){//若需要修改状态为取消，需要联系客服进行取消
                //这里之后再补充
                return R.failed(null,"请联系客服进行订单取消");
            }else {//可直接修改的情况:直接更新数据库
                if (serviceCenter.updateMySql(operation)) {
                    return R.ok();
                }
                return R.failed(null, "数据更新异常");
            }
        }else{
            return R.failed(null,"您无权修改执行订单状态");
        }
    }

    /**
     * 提交兼职订单反馈：
     * 若feedback为空，无法提交反馈
     */
    @ApiOperation("提交兼职订单反馈")
    @GetMapping("/updateJobFeedback")
    public R updateJobStatus(@RequestHeader("uid") String postId, @RequestParam("operationId") String operationId, @RequestParam("feedback") String feedback) {
        Operation operation = FormTemplate.analyzeTemplate(serviceCenter.selectMySql(operationId, Operation.class), Operation.class);
        assert operation != null;
        if (feedback != null) {
            if (postId.equals(operation.getPublisherId())) {
                operation.setFeedback_from_publisher_to_applicant(feedback);
            } else operation.setFeedback_from_applicant_to_publisher(feedback);
            if (serviceCenter.update(operation)) {// 调用套件
                log.info("更新成功");
                return R.ok();
            } else return R.failed();
        } else return R.failed(null,"反馈内容为空");
    }
}

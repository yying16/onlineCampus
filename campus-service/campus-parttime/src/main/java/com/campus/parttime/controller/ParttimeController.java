package com.campus.parttime.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.shaded.com.google.gson.JsonObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.common.util.TimeUtil;
import com.campus.message.dto.PromptInformationForm;
import com.campus.parttime.dao.*;
import com.campus.parttime.domain.*;
import com.campus.parttime.dao.JobDao;
import com.campus.parttime.dao.OperationDao;
import com.campus.parttime.domain.Apply;
import com.campus.parttime.domain.Breaker;
import com.campus.parttime.domain.Job;
import com.campus.parttime.domain.Operation;
import com.campus.parttime.dto.*;
import com.campus.parttime.feign.MessageClient;
import com.campus.parttime.pojo.FavoritesList;
import com.campus.parttime.pojo.MonthlyStatistics;
import com.campus.parttime.vo.JobStatusUpdateForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.Op;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.campus.user.domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.campus.parttime.constant.ApplyStatus.*;
import static com.campus.parttime.constant.JobStatus.*;
import static com.campus.parttime.constant.OperationStatus.*;

/**
 * author kakakaka
 */

@RestController
@RequestMapping("/parttime")
@Slf4j
@Api("兼职模块接口")
public class ParttimeController {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    ApplyDao applyDao;

    @Autowired
    JobDao jobDao;

    @Autowired
    OperationDao operationDao;

    @Autowired
    LikeDao likeDao;

    @Autowired
    FavoritesDao favoritesDao;

    @Autowired
    RecordDao recordDao;

    @Autowired
    MessageClient messageClient;

    /**
     * 发布兼职(已测试通过)
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

    @ApiOperation("发布者查看发布的兼职列表")
    @GetMapping("/searchJobListToPublisher")
    public R searchJobListToPublisher(@RequestHeader("uid")String publisherId) {
        List<Job> applyList = jobDao.searchJobList(publisherId);
        if(applyList!=null){
            return R.ok(applyList,"查找成功");
        }
        return R.ok(null,"无申请记录");
    }

    /**
     * 普通更新
     * 无权修改状态为完成或者招满
     * 若applyNum==0 可以直接更新Job信息
     * 若applyNum>0&&passedNum==0,返回强制更新确认信息给前端
     * 若passedNum>0,则返回无法更新
     */
    @ApiOperation("普通更新兼职信息")
    @PostMapping("/updateJobInfo")
    public R updateJob(@RequestBody JobUpdateForm form) {
        Job job = FormTemplate.analyzeTemplate(form, Job.class);
        assert job != null;
        Job jobSql = (Job) serviceCenter.selectMySql(job.getJobId(),Job.class);
        assert jobSql != null;
        if(jobSql.getApplyNum().equals(0)) {// 若applyNum==0，直接更新Job信息
            if(job.getStatus()!=null&&((job.getStatus().equals(FINISH.code)||job.getStatus().equals(FULL.code)))) {
                return R.failed(null,"您没有设置状态为完成或者招满的权限");
            }
            if (serviceCenter.update(job)) {// 调用套件
                return R.ok();
            }
            return R.failed();
        }
        if(jobSql.getPassedNum()>0){
            return R.failed(null,"已有申请通过，无法更新兼职");
        }
        return R.failed(null,"是否强制更新信息？");
    }

    /**
     * 强制更新(也包含了修改状态为已关闭)
     * 若applyNum>0&&passedNum==0,进行强制更新
     * 修改：applyNum=0;
     * 注意：兼职状态无法修改为除了关闭以外的状态
     * 更新：job数据（update到缓存和数据库）
     * 更新成功后删除：与当前job绑定的所有apply记录
     * 后端调用message模块的sendPromptInformation方法发送提示信息给申请者
     * 返回：vo对象
     */
    @ApiOperation("强制更新兼职信息")//待修改
    @PostMapping("/forceUpdateJobInfo")
    public R forceUpdateJob(@RequestBody JobUpdateForm form) {
        Job job = FormTemplate.analyzeTemplate(form, Job.class);
        assert job != null;
        Job sqlJob = (Job)serviceCenter.selectMySql(job.getJobId(),Job.class);
        assert sqlJob != null;
        if(sqlJob.getApplyNum()>0 && sqlJob.getPassedNum().equals(0)){

            //修改applyNum=0
            job.setApplyNum(0);
            //兼职状态无法修改为除了关闭以外的状态
            if(job.getStatus()!=null && !Objects.equals(job.getStatus(), CLOSE.code)){
                return R.failed(null,"您没有权限修改兼职状态");
            }
            //更新job数据（update到缓存和数据库）
            if (serviceCenter.update(job)) {

                //逻辑删除与当前job绑定的所有apply记录
                applyDao.deleteApplyByJobId(job.getJobId());

                //后端调用message模块的sendPromptInformation方法发送提示信息给申请者
                List<String> applicantIds = applyDao.selectByJobId(job.getJobId());
                for(String applicantId : applicantIds){
                    messageClient.sendPromptInformation(new PromptInformationForm(applicantId,"兼职信息已修改，请重新申请！"));
                }
                return R.ok();
            }
            return R.failed(null,"强制更新兼职信息失败");
        }
        return R.failed();
    }



    /**
     * 删除兼职(需进一步修改收藏部分)
     * 兼职状态已完成或者已关闭
     * 逻辑删除与当前job绑定的所有like记录
     */
    @ApiOperation("删除兼职")
    @GetMapping("/deleteJob")
    public R deleteJob(@RequestParam("jobId") String jobId) {
        Job job = (Job)serviceCenter.search(jobId,Job.class);
        if(job.getStatus().equals(FINISH.code) || job.getStatus().equals(CLOSE.code)){
            if (serviceCenter.delete(jobId, Job.class)) {// 调用套件

                //处理点赞记录:逻辑删除与当前job绑定的所有like记录
                likeDao.deleteLikeByJobId(jobId);

                //处理收藏记录:逻辑删除与当前job绑定的所有favorites记录,并用后端调用message模块的sendPromptInformation方法发送提示信息给申请者
                favoritesDao.deleteFavoritesByJobId(jobId);

                List<String> collectors = favoritesDao.selectCollectorsByJobId(jobId);
                for(String collector : collectors){
                    messageClient.sendPromptInformation(new PromptInformationForm(collector,"您曾收藏的兼职已删除"));
                }
                return R.ok();
            }
            return R.failed("兼职删除失败");
        }
        return R.failed("当前兼职状态不为关闭或未完成，无法删除");
    }

    /**
     * 提交兼职申请
     * 若兼职状态为关闭或者招满,无法提交兼职申请;
     * 创建兼职申请记录:初始化基本信息,修改初始状态为已申请,生成主键Id;
     * 修改Job表兼职申请人数;
     * 将该记录插入数据库中
     */
    @ApiOperation("提交兼职申请")
    @GetMapping("/addJobApply")
    public R addJobApply(@RequestHeader("uid") String applicantId, @RequestParam("jobId") String jobId) {
        Job job = (Job) serviceCenter.search(jobId, Job.class);
        assert job!=null;
        // 若当前兼职状态为关闭或者招满，兼职申请提交失败
        if(applyDao.selectCreditByJobId(applicantId)<=0){
            return R.failed(null,"信用值不足，无法提交申请");
        }
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
                messageClient.sendPromptInformation(new PromptInformationForm( job.getPublisherId(),"已有用户"+apply.getApplicantId()+"提交申请，请及时查看！"));
                return R.ok(apply.getApplicationId(),"提交成功");
            }
            return R.failed(null,"提交失败,请重试");
        }
        return R.failed(null,"提交失败,请重试");
    }

    /**
     * 删除兼职申请记录
     * 若兼职申请状态为已通过,无法删除兼职申请记录;
     * 删除数据库中的兼职申请记录;
     * 修改Job表的申请人数（申请人数-1）;
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

    @ApiOperation("兼职者查看兼职申请列表")
    @GetMapping("/searchApplyListToApplicant")
    public R searchApplyListToApplicant(@RequestHeader("uid")String applicantId) {
        List<Apply> applyList = applyDao.searchApplyListByApplicantId(applicantId);
        if(applyList!=null){
            return R.ok(applyList,"查找成功");
        }
        return R.ok(null,"无申请记录");
    }

    @ApiOperation("发布者查看兼职申请列表")
    @GetMapping("/searchApplyListToPublisher")
    public R searchApplyListToPublisher(@RequestHeader("uid")String userId, @RequestParam("jobId") String jobId) {
        Job job = (Job) serviceCenter.selectMySql(jobId, Job.class);
        if(userId.equals(job.getPublisherId())){// 发布者查看当前兼职申请列表
            List<Apply> applyList = jobDao.SearchApplyListByJobId(jobId);
            return R.ok(applyList);
        }
        return R.failed(null,"查看失败");
    }

    /**
     * 通过兼职申请
     * 若兼职申请记录为已删除,无法通过兼职申请;
     * 若当前兼职状态为招满，无法通过兼职申请;
     * 修改当前apply记录的状态为已通过;
     * 修改当前job记录的获得通过人数（passedNum+1）;
     * 若通过该申请后，兼职招满了，就设置当前的兼职状态为招满;
     * 将修改后的apply和job记录更新到数据库中;
     * 若更新成功，新增执行订单operation记录，初始化operation的初始化信息，并插入数据库中
     */
    @ApiOperation("通过兼职申请")
    @GetMapping("/passApply")
    @Transactional
    public R passApply(@RequestParam("applicationId") String applicationId) {
        Apply apply = (Apply) serviceCenter.selectMySql(applicationId, Apply.class);
        //申请前判断当前申请是否删除
        if(apply==null){
            log.info("兼职记录不存在，无法通过该兼职");
            return R.failed(null,"兼职记录不存在，无法通过该兼职");
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
                messageClient.sendPromptInformation(new PromptInformationForm(operation.getApplicantId(),"您已通过兼职申请，请按时完成您的任务！"));
                return R.ok();
            }
            else return R.failed(null,"兼职执行记录生成异常");
        }
        log.info("数据更新异常，请重试!");
        return R.failed(null, "数据更新异常，请重试!");
    }

    /**
     * 拒绝兼职申请
     * 根据表单中的applicationId查找数据库对应的申请记录。若当前记录的申请状态为已通过，无法拒绝该兼职申请;
     * 直接更新数据库
     */
    @ApiOperation("拒绝兼职申请")
    @GetMapping("/rejectApply")
    public R rejectApply(@RequestParam("applicationId") String applicationId) {
        Apply apply = (Apply) serviceCenter.selectMySql(applicationId,Apply.class);
        if(apply.getStatus().equals(PASSED.code)){
            return R.failed(null,"申请已通过，拒绝失败");
        }
        apply.setStatus(REFUSED.code);
        if (serviceCenter.updateMySql(apply)) {// 调用套件
            messageClient.sendPromptInformation(new PromptInformationForm(apply.getApplicantId(),"很遗憾，您未通过本次兼职申请！"));
            return R.ok();
        }
        return R.failed(null,"数据更新异常，请重试!");
    }

    /**
     * 修改兼职操作状态
     * (需要解决的问题)：若当前状态修改为确认完成,要将job记录中finishNum+1，并且要判断整个job记录是否全部完成
     * 根据发起修改的ID进行判断：
     * （无权）若当前Id为兼职发布者，则无权修改执行订单状态为完成；若当前Id为兼职执行者，则无权修改执行订单状态为确认完成；若为其他Id，则无权进行所有状态的修改
     * （可修改）若当前Id为兼职发布者，可以将执行订单状态直接修改为确认完成；若当前Id为兼职执行者，可以将执行订单状态直接修改为已完成；（直接更新数据库）
     * （其他处理）若当前Id为兼职执行者或者兼职发布者，想修改执行订单状态为取消,系统会提示取消需要联系客服处理。
     *
     */
    @ApiOperation("修改兼职操作状态")
    @GetMapping("/updateOperationStatus")
    public R updateOperationStatus(@RequestHeader("uid") String posterId,@RequestBody OperationStatusUpdateForm form) {
        Operation operation = FormTemplate.analyzeTemplate(form,Operation.class);
        assert operation != null;
        Operation operationSql = (Operation) serviceCenter.selectMySql(operation.getOperationId(),Operation.class);

        if(posterId.equals(operationSql.getApplicantId()) || posterId.equals(operationSql.getPublisherId())){//判断当前Id是否为兼职发布者或执行者Id
            if(posterId.equals(operationSql.getApplicantId()) && operation.getStatus().equals(CONFIRM.code)) {//若当前Id为执行者并且需要修改状态为确认完成
                return R.failed(null, "您没有权限确认订单完成");
            }
            if(posterId.equals(operationSql.getPublisherId()) && operation.getStatus().equals(COMPLETED.code)){//若当前Id为发布者并且需要修改状态为完成
                return R.failed(null, "您没有权限完成订单");
            }
            if(operation.getStatus().equals(CANCEL.code)){//若需要修改状态为取消，需要联系客服进行取消
                //这里之后再补充
                return R.failed(null,"请联系客服进行订单取消");
            }else {//可直接修改的情况:直接更新数据库
                if(posterId.equals(operationSql.getPublisherId()) && operation.getStatus().equals(CONFIRM.code)) {
                    Job job = (Job)serviceCenter.selectMySql(operation.getJobId(),Job.class);
                    assert job != null;
                    //订单由发布者确认完成后，执行者信用值+5
                    applyDao.addCreditByJobId(operation.getJobId());
                    job.setFinishNum(job.getFinishNum() + 1);
                    if (job.getFinishNum().equals(job.getRecruitNum())) {
                        job.setStatus(FINISH.code);
                    }
                    if(!serviceCenter.updateMySql(job)){
                        return R.failed(null,"兼职数据更新异常");
                    }
                }
                if (serviceCenter.updateMySql(operation)) {
                    return R.ok();
                }
                return R.failed(null, "执行数据更新异常");
            }
        }else{
            return R.failed(null,"您无权修改执行订单状态");
        }
    }

    /**
     * 提交兼职订单反馈
     * 若feedback为空，无法提交反馈
     */
    @ApiOperation("提交兼职订单反馈")
    @GetMapping("/updateJobFeedback")
    public R updateJobStatus(@RequestHeader("uid") String postId, @RequestParam("operationId") String operationId, @RequestParam("feedback") String feedback) {
        Operation operation = FormTemplate.analyzeTemplate(serviceCenter.selectMySql(operationId, Operation.class), Operation.class);
        assert operation != null;
        if (feedback != "") {
            if (postId.equals(operation.getPublisherId())) {
                operation.setFeedback_from_publisher_to_applicant(feedback);
            } else operation.setFeedback_from_applicant_to_publisher(feedback);
            if (serviceCenter.update(operation)) {// 调用套件
                log.info("更新成功");
                return R.ok();
            } else return R.failed();
        } else return R.failed(null,"反馈内容为空");
    }

    @ApiOperation("条件查询兼职")
    @PostMapping("/searchJob")
    public R searchRecruit(@RequestBody Map condition) {
        List search = serviceCenter.search(condition, Job.class);
        if (search != null) {
            return R.ok(search);
        }
        return R.failed();
    }

    @ApiOperation("兼职首页懒加载")
    @GetMapping("/lazyLoading")
    public R lazyLoading(@RequestParam("num") Integer num) {
        List<Job> jobs = serviceCenter.loadData(num,Job.class);
        if (jobs != null) {
            return R.ok(jobs);
        }
        return R.failed();
    }

    /**
     * 查看兼职详情
     */
    @ApiOperation("查看兼职详情")
    @GetMapping("/getJobDetail")
    public R getJobDetail(@RequestHeader("uid")String userId, @RequestParam("jobId") String jobId) {
        Job job = (Job)serviceCenter.search(jobId, Job.class);
        if (job != null) {
            if(recordDao.searchRecordIsExist(jobId,userId)!=null){// 访问记录存在
                recordDao.updateRecoreScore(jobId,userId);// 将对应的记录中的score+1;
                incrementVisitNum(jobId);
                return R.ok(job);
            }
            else{ // 创建一个新记录
                Record record = new Record();
                record.setJob_record_id(IdWorker.getIdStr(record));
                record.setJob_id(jobId);
                record.setUser_id(userId);
                record.setScore(1.0);
                if(serviceCenter.insertMySql(record)){
                    incrementVisitNum(jobId);
                    return R.ok(job);
                }
            }
        }
        return R.failed(null,"当前兼职不存在");
    }

    /**
     * 查看兼职申请详情
     * 只有申请提交者才能看到兼职申请详情
     */
    @ApiOperation("查看兼职申请详情")
    @GetMapping("/getApplyDetail")
    public R getApplyDetail(@RequestHeader("uid")String userId, @RequestParam("applicationId") String applicationId) {
        Apply apply = (Apply) serviceCenter.selectMySql(applicationId, Apply.class);
        if (apply != null && apply.getApplicantId().equals(userId)) {
            return R.ok(apply);
        }
        return R.failed(null,"申请详情查看异常");
    }

    @ApiOperation("查看执行订单详情")
    @GetMapping("/getOperationDetail")
    public R getOperationDetail(@RequestParam("operationId") String operationId) {
        Operation operation = (Operation) serviceCenter.search(operationId, Operation.class);
        if (operation != null) {
            return R.ok(operation);
        }
        return R.failed(null,"执行订单详情查看异常");
    }

    @ApiOperation("新增兼职访问量")
    @GetMapping("/addVisitNum")
    public R incrementVisitNum(@RequestParam("jobId") String jobId){
        if(serviceCenter.increment(jobId,Job.class,true,"visitNum")){
            return R.ok();
        }
        return R.failed();
    }

    /**
     * 查看兼职申请详情
     * 只有管理员/客服才能取消兼职申请
     */
    @ApiOperation("取消兼职申请")
    @GetMapping("/cancelJobOperation")
    public R cancelJobOperation(@RequestParam("userId") String userId,@RequestParam("operationId") String operationId){
        Operation operation = (Operation) serviceCenter.selectMySql(operationId, Operation.class);
        operation.setStatus(2);
        if(userId.equals(operation.getPublisherId())||userId.equals(operation.getApplicantId())){
            applyDao.subCreditByJobId(userId);
        }
        if(!serviceCenter.updateMySql(operation)){
            return R.failed(null,"取消失败，请重试");
        }
        return R.ok();
    }

    @ApiOperation("个人数据统计")
    @GetMapping("/personalStatistics")
    public R personalStatistics(@RequestParam("userId")String userId){
        Map<String,Object> map = new HashMap<String,Object>();
        // 获取个人执行完成率
        if(operationDao.searchPersonalCompletionRate(userId)==null){
            map.put("completionRate",0);
        }
        else map.put("completionRate",operationDao.searchPersonalCompletionRate(userId));

        // 获取个人发布兼职总计
        if(jobDao.searchPersonalPostJobNum(userId)==null){
            map.put("postJobNum",0);
        }
        else map.put("postJobNum",jobDao.searchPersonalPostJobNum(userId));

        // 获取申请兼职总计
        if(applyDao.searchPersonalApplyJobNum(userId)==null){
            map.put("applyJobNum",0);
        }
        else map.put("applyJobNum",applyDao.searchPersonalApplyJobNum(userId));
        return R.ok(map);
    }

    @ApiOperation("管理员数据统计")
    @GetMapping("/administratorStatistics")
    //统计月度执行完成率
    public R administratorStatistics(@RequestParam("year")Integer year,@RequestParam("month") Integer month){
        String begin = String.format("%04d-%02d-01 00:00:00",year,month);
        String end = String.format("%04d-%02d-%02d 00:00:00",year,month, TimeUtil.getLastDay(year,month));
        List<MonthlyStatistics> monthlyStatisticsLists = operationDao.searchPublicCompletionRate(begin,end);
        if(monthlyStatisticsLists!=null){
            return R.ok(monthlyStatisticsLists);
        }
        return R.failed();
    }

    @ApiOperation("用户点赞操作")
    @GetMapping("/likeJob")
    public R likeJob(@RequestHeader("uid")String userId,@RequestParam("jobId") String jobId){
        Job job = (Job)serviceCenter.selectMySql(jobId,Job.class);
        if(job==null){
            return R.failed(null,"当前兼职记录不存在，无法点赞");
        }
        String likeId = likeDao.searchLikeIsExist(userId,jobId);
        if(likeId==null){
            Like like = new Like();
            like.setLikeId(IdWorker.getIdStr(like));
            like.setUserId(userId);
            like.setJobId(jobId);
            if(serviceCenter.insertMySql(like)){
                job.setLikeNum(job.getLikeNum()+1);
                if(!serviceCenter.updateMySql(job)){
                    return R.failed(null,"更新兼职信息失败");
                }
                return R.ok(null,"点赞成功");
            }
        }
        return R.failed(null,"您已为该兼职点赞了，是否需要取消点赞？");
    }

    @ApiOperation("用户取消点赞操作")
    @GetMapping("/cancelLikeJob")
    public R cancelLikeJob(@RequestHeader("uid")String userId,@RequestParam("jobId") String jobId){
        Job job = (Job)serviceCenter.selectMySql(jobId,Job.class);
        // 判断该用户点赞的兼职记录是否被删除
        if(job==null){
            return R.failed(null,"当前兼职记录不存在");
        }
        // 兼职未被删除
        String likeId = likeDao.searchLikeIsExist(userId,jobId); // 查找对应的点赞记录Id
        // 若该用户点赞过该兼职，则取消点赞
        if(likeId!=null){
            Like like = (Like)serviceCenter.selectMySql(likeId,Like.class); // 通过id查找该点赞记录
            like.setDeleted(true); // 修改delete,删除该点赞记录
            if(!serviceCenter.updateMySql(like)){ // 存入数据库中
                return R.failed(null,"点赞信息更新失败");
            }
            //修改job记录中的likeNum
            job.setLikeNum(job.getLikeNum()-1);
            if(!serviceCenter.updateMySql(job)){ // 存入数据库中
                return R.failed(null,"更新兼职信息失败");
            }
        }
        return R.failed(null,"取消点赞失败");
    }

    @ApiOperation("用户收藏操作")
    @GetMapping("/FavoritesJob")
    public R FavoritesJob(@RequestHeader("uid")String userId,@RequestParam("jobId") String jobId){
        Job job = (Job)serviceCenter.selectMySql(jobId,Job.class);
        if(job==null){
            return R.failed(null,"当前兼职记录不存在，无法收藏");
        }
        String favoritesId = favoritesDao.searchFavoritesIsExist(userId,jobId);
        if(favoritesId==null){ // 若没有存在收藏记录，则新建收藏记录
            Favorites favorites = new Favorites();
            favorites.setFavoritesId(IdWorker.getIdStr(favorites));
            favorites.setUserId(userId);
            favorites.setJobTitle(job.getJobTitle());
            favorites.setJobId(jobId);
            if(serviceCenter.insertMySql(favorites)){ // 将收藏记录存入数据库
                job.setFavoritesNum(job.getFavoritesNum()+1); // 修改对应兼职记录的收藏人数
                if(!serviceCenter.updateMySql(job)){ // 将修改后的兼职记录更新到数据库
                    return R.failed(null,"更新兼职信息失败");
                }
                return R.ok(null, "收藏成功");
            }

        }
        return R.failed(null,"您已收藏过该兼职了，是否需要取消收藏？");
    }

    @ApiOperation("用户取消收藏操作")
    @GetMapping("/cancelFavoritesJob")
    public R cancelFavoritesJob(@RequestHeader("uid")String userId,@RequestParam("jobId") String jobId){
        Job job = (Job)serviceCenter.selectMySql(jobId,Job.class);
        // 判断该用户收藏的兼职记录是否被删除
        if(job==null){
            return R.failed(null,"当前兼职记录不存在");
        }
        // 兼职未被删除
        String favoritesId = favoritesDao.searchFavoritesIsExist(userId,jobId); // 查找对应的收藏记录Id
        // 若该用户收藏过该兼职，则取消收藏
        if(favoritesId!=null){
            Favorites favorites = (Favorites) serviceCenter.selectMySql(favoritesId,Favorites.class); // 通过id查找该收藏记录
            favorites.setDeleted(true); // 修改delete,删除该收藏记录
            if(!serviceCenter.updateMySql(favorites)){ // 更新到数据库中
                return R.failed(null,"收藏信息更新失败");
            }
            //修改job记录中的favoritesNum
            job.setFavoritesNum(job.getFavoritesNum()-1);
            if(!serviceCenter.updateMySql(job)){ // 存入数据库中
                return R.failed(null,"更新兼职信息失败");
            }
        }
        return R.failed(null,"取消收藏失败");
    }

    @ApiOperation("查看用户收藏列表")
    @GetMapping("/searchFavoritesList")
    public R searchFavoritesList(@RequestParam("userId") String userId) {
        List<FavoritesList> favoritesList = favoritesDao.SearchFavoritesByUserId(userId);
        return R.ok(favoritesList);
    }
}

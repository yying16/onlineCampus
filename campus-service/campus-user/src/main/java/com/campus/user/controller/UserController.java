package com.campus.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.FormTemplate;
import com.campus.common.util.R;
import com.campus.user.dao.BreakerDao;
import com.campus.user.domain.Breaker;
import com.campus.user.domain.Report;
import com.campus.user.domain.User;
import com.campus.user.dto.*;
import com.campus.user.feign.MessageClient;
import com.campus.user.pojo.PromptInformationForm;
import com.campus.user.service.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.campus.common.constant.InterfaceRefresh.REFRESH_MAIL;

@RestController
@RequestMapping("/user")
@Log4j2
@Api(tags = "用户数据相关接口")
public class UserController {

    @Autowired
    ServiceCenter serviceCenter;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    UserServiceImpl userService;

    //模板引擎
    @Autowired
    TemplateEngine templateEngine;

    @Autowired
    MessageClient messageClient;

    @Autowired
    BreakerDao breakerDao;

    @Value("${email.baseurl}")
    private String baseUrl;

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getDetail")
    public R getDetail(@RequestHeader("uid") String uid) {
        String userStr = String.valueOf(redisTemplate.opsForValue().get("user"+uid));
        User user = JSONObject.parseObject(userStr, User.class);
        if (user == null) {
            return R.failed("令牌无效");
        }
        return R.ok(user);
    }


    //根据用户id获取用户信息
    @ApiOperation(value = "根据用户id获取用户信息")
    @GetMapping("{userId}")
    public R getUserById(@ApiParam("用户id") @PathVariable("userId") String userId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        User user = userService.getOne(wrapper);
        if (user != null) {
            return R.ok(user);
        } else {
            return R.failed("用户不存在");
        }
    }


    //更新用户信息
    @ApiOperation(value = "更新用户信息")
    @PostMapping("/updateUser")
    public R updateUser(@ApiParam("用户对象") @RequestBody UpdateUserForm updateUserForm) {
        User user = new User();
        BeanUtils.copyProperties(updateUserForm, user);
        boolean b = userService.updateById(user);
        if (b) {
            return R.ok();
        } else {
            return R.failed();
        }
    }


    /**
     * 修改密码
     */
    @ApiOperation(value = "修改密码")
    @PostMapping("/updatePassword")
    public R updatePassword( @ApiParam("对象：包括password") @RequestBody UpdatePasswordForm form) {
        boolean b = userService.updatePassword(form);
        if (b) {
            return R.ok();
        }
        return R.failed();
    }


    /**
     * 发送邮箱激活邮件
     */
    @ApiOperation(value = "发送邮箱激活邮件")
    @GetMapping("/sendVerifyLink/{email}")
    public R sendVerifyLink(@ApiParam("邮箱") @PathVariable String email, HttpServletRequest request) {


        //判断邮箱是否已经被绑定
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return R.failed("邮箱已经被绑定");
        }

        //从redis获取验证码，如果获取到直接返回
        String link = redisTemplate.opsForValue().get(email + "_link");
        if (StringUtils.hasLength(link)) {
            //如果有说明现在不是第一次发送，返回新的验证码
            //删除旧的验证码
            redisTemplate.delete(email + "_link");
        }

        String uid = request.getHeader("uid");

        //生成邮件链接
        link = baseUrl + "/user/verifyEmail?email=" + email + "&userId=" +uid;

        //创建邮件上下文
        Context context = new Context();
        context.setVariable("link", link);//设置验证邮件链接
        context.setVariable("operate", "激活邮箱");//设置操作类型

        //将模板引擎内容解析成html字符串
        String emailContent = templateEngine.process("linkEmailTemplate", context);

        //发送邮件
        boolean b = userService.sendEmail(emailContent, email);

        if (b) {
            //发送成功，放到redis里面
            //设置有效时间5分钟
            redisTemplate.opsForValue().set(email + "_link", link, 60, TimeUnit.MINUTES);
            log.info("邮件链接:" + link);
            return R.ok(null, "邮件激活链接发送成功");
        } else {
            return R.failed(null, "邮件激活链接发送失败");
        }
    }


    /**
     * 发送邮件
     */
    @ApiOperation(value = "发送邮件")
    @PostMapping("/sendEmail")
    public R sendEmail(@RequestBody Map<String,String> map) {

        String content = map.get("content");
        String uid = map.get("uid");

        //根据uid获取用户信息
        User user = userService.getById(uid);
        if (user == null) {
            return R.failed("用户不存在");
        }
        String email = user.getEmail();
        if (StringUtils.isEmpty(email)) {
            return R.failed("用户未绑定邮箱");
        }

        String username = user.getUsername();

        //将内容封装到邮件模板中，发送邮件
        Context context = new Context();
        context.setVariable("content", content);//设置邮件内容
        context.setVariable("username", username);//设置接收方用户名

        //将模板引擎内容解析成html字符串
        String emailContent = templateEngine.process("informEmailTemplate", context);

        boolean b = userService.sendEmail(emailContent, email);
        if (b) {
            return R.ok(null, "邮件发送成功");
        } else {
            return R.failed(null, "邮件发送失败");
        }
    }


    /**
     * 激活邮箱
     */
    @ApiOperation(value = "激活邮箱")
    @GetMapping("/verifyEmail")
    public String verifyEmail(@ApiParam("邮箱") @Param("email") String email, @ApiParam("用户id") @Param("userId") String userId) {

        //查看redis中是否有验证链接
        String link = redisTemplate.opsForValue().get(email + "_link");
        //如果没有说明验证链接已经过期
        if (!StringUtils.hasLength(link)) {

            Context context = new Context();
            //将模板引擎内容解析成html字符串
            String emailContent = templateEngine.process("verifyLinkExpired", context);
            return emailContent;
        }
        //如果有，说明验证链接有效，删除redis中的验证链接
        redisTemplate.delete(email + "_link");
        //激活邮箱,设置邮箱
        userService.activateEmail(email, userId);
        Context context = new Context();
        //将模板引擎内容解析成html字符串
        context.setVariable("email", email);
        String emailContent = templateEngine.process("verifySuccess", context);

        //需要通知绑定邮箱界面，用户邮箱已经激活成功，进行刷新或跳转
        messageClient.sendPromptInformation(new PromptInformationForm(userId, REFRESH_MAIL.code));


        //返回邮件激活成功页面
        return emailContent;
    }


    /**
     * 根据账号获取用户信息（手机号和邮箱）
     */
    @ApiOperation(value = "根据账号获取用户信息")
    @GetMapping("/getUserByAccount/{account}")
    public R getUserByAccount(@ApiParam("账号") @PathVariable("account") String account) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("account", account);
        wrapper.select("telephone", "email");
        User user = userService.getOne(wrapper);
        if (user != null) {
            return R.ok(user);
        } else {
            return R.failed(null,"用户不存在");
        }
    }

    /**
     * 查看用户手机验证码是否正确
     */
    @ApiOperation(value = "查看用户验证码是否正确")
    @PostMapping("/checkPhoneCode")
    public R checkCode( @RequestBody CheckCodeForm form) {
        boolean b = userService.checkCode(form);
        if (b) {
            return R.ok();
        } else {
            return R.failed();
        }
    }

    /**
     * 查看用户邮箱验证码是否正确
     */
    @ApiOperation(value = "查看用户邮箱验证码是否正确")
    @PostMapping("/checkEmailCode")
    public R checkEmailCode( @RequestBody CheckEmailCodeForm form) {
        boolean b = userService.checkEmailCode(form);
        if (b) {
            return R.ok();
        } else {
            return R.failed();
        }
    }

    /**
     * 举报用户操作
     */
    @ApiOperation("举报用户操作")
    @GetMapping("/reportUser")
    public R reportUser(@RequestBody ReportInsertForm form ){
        Report report = FormTemplate.analyzeTemplate(form, Report.class);
        assert report!=null;
        report.setReportId(IdWorker.getIdStr(report));
        report.setReport_status(0);
        if(!serviceCenter.insertMySql(report)) {
            return R.failed(null,"举报失败，请重试");
        }
        return R.ok(null,"举报信息提交成功");
    }

    /**
     * 举报状态修改：
     * 举报状态（0-审核中，1-确认，2-驳回）
     * 若状态为确认，则调用addBreaker方法新增违规用户记录。
     */
    @ApiOperation("举报状态修改")
    @GetMapping("/updateReportStatus")
    public R updateReportStatus(@RequestParam("reportId") String reportId, @RequestParam("reportStatus")Integer reportStatus){
        Report report = (Report) serviceCenter.selectMySql(reportId,Report.class);
        if(report==null){
            return R.failed(null,"举报不存在");
        }
        report.setReport_status(reportStatus);
        if(!serviceCenter.updateMySql(report)) {
            if(reportStatus==1){ // 当管理员通过举报请求后
                addBreaker(report.getReported_id(),report.getReport_content());// 调用addBreak方法将该用户添加到违规用户列表中
            }
            return R.failed(null,"举报状态修改失败,请重试");
        }
        return R.ok(null,"举报状态修改成功");
    }

    /**
     * 违规用户处理
     * 需要考虑违规表的breakNum是否要删除，因为user表中也有breakNum;
     * 还需要添加（breaker表的模块字段：以及举报类型;方法：搜索某一用户的所有违规详情;数据统计：违规次数最多的用户排名）
     */
    @ApiOperation("违规用户处理")
    @GetMapping("/addBreaker")
    public R addBreaker(@RequestParam("breakerId")String breakerId, @RequestParam("breakText")String breakText){
        User user = (User)serviceCenter.selectMySql(breakerId,User.class);
        if(user==null){
            return R.failed(null,"该用户不存在");
        }
        Breaker breaker = new Breaker();
        breaker.setBreakId(IdWorker.getIdStr(breaker));
        breaker.setBreakerId(breakerId);
        breaker.setBreakerAccount(user.getAccount());
        breaker.setBreakerName(user.getUsername());
        breaker.setBreakText(breakText);
        if(serviceCenter.insertMySql(breaker)) {
            user.setBreakNum(user.getBreakNum()+1);
            if(!serviceCenter.updateMySql(user)){
                return R.failed(null,"用户记录更新失败");
            }
            return R.ok();
        }else return R.failed(null,"更新失败，请重试");
    }

    /**
     * 违规用户列表（管理员查看：违规次数最多的用户排名）
     * 还需要添加（方法：点击某一用户的所有违规详情;数据统计：违规次数最多的用户排名）
     */

    /**
     * 查看某一用户的违规详情
     */
}

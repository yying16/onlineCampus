package com.campus.user.controller;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.user.domain.User;
import com.campus.user.dto.UpdatePasswordForm;
import com.campus.user.service.impl.UserServiceImpl;
import com.campus.user.util.EmailCodeUtil;
import com.campus.user.util.TokenUtil;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Log4j2
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

    @Value("${email.baseurl}")
    private String baseUrl;

    @GetMapping("/getDetail")
    public R getDetail(@RequestHeader("token") String token) {
        User user = TokenUtil.getClaimsFromToken(token);
        if (user == null) {
            return R.failed("令牌无效");
        }
        if (Objects.equals(redisTemplate.opsForValue().get(user.getUserId()), token)) {
            return R.ok(user);
        } else {
            return R.failed("令牌无效");
        }

    }


    /**
     * 获取系统中所有普通用户id
     */
    @GetMapping("/getAllUserId")
    public R getAllUserId() {
        List<String> allUserId = userService.getAllUserId();
        if (allUserId == null || allUserId.size() == 0) {
            return R.failed();
        }
        return R.ok(allUserId);
    }

    /**
     * 获取用户的自动回复内容
     */
    @GetMapping("/getAutoReply/{userId}")
    public R getAutoReply(@PathVariable String userId) {
        String ret = userService.getAutoReply(userId);
        if(ret==null){
            return R.failed();
        }
        return R.ok();
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword/{userId}")
    public R updatePassword(@PathVariable(value = "userId") String userId, @RequestBody UpdatePasswordForm form) {
        boolean b = userService.updatePassword(userId, form);
        if (b) {
            return R.ok();
        }
        return R.failed();
    }


    /**
     * 发送邮箱激活邮件
     */
    @GetMapping("/sendVerifyLink/{email}")
    public R sendVerifyLink(@PathVariable String email, HttpServletRequest request) {


        //判断邮箱是否已经被绑定
        User user = userService.getUserByEmail(email);
        if (user != null) {
            return R.failed("邮箱已经被绑定");
        }

        //从redis获取验证码，如果获取到直接返回
        String link = redisTemplate.opsForValue().get(email+"_link");
        if (StringUtils.hasLength(link)) {
            //如果有说明现在不是第一次发送，返回新的验证码
            //删除旧的验证码
            redisTemplate.delete(email+"_link");
        }

        String token = request.getHeader("token");

        if (token == null) {
            return R.failed("令牌无效");
        }

        User tokenUser = TokenUtil.getClaimsFromToken(token);

        //生成邮件链接
        link = baseUrl+ "/user/verifyEmail?email=" + email+"&userId="+ tokenUser.getUserId();

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
            redisTemplate.opsForValue().set(email+"_link", link, 60, TimeUnit.MINUTES);
            log.info("邮件链接:"+link);
            return R.ok(null, "邮件激活链接发送成功");
        }else {
            return R.failed(null, "邮件激活链接发送失败");
        }
    }


    /**
     * 激活邮箱
     */
    @GetMapping("/verifyEmail")
    public String verifyEmail(@Param("email") String email,@Param("userId") String userId) {

        //查看redis中是否有验证链接
        String link = redisTemplate.opsForValue().get(email+"_link");
        //如果没有说明验证链接已经过期
        if (!StringUtils.hasLength(link)) {

            Context context = new Context();
            //将模板引擎内容解析成html字符串
            String emailContent = templateEngine.process("verifyLinkExpired", context);
            return emailContent;
        }
        //如果有，说明验证链接有效，删除redis中的验证链接
        redisTemplate.delete(email+"_link");
        //激活邮箱,设置邮箱
        userService.activateEmail(email,userId);
        Context context = new Context();
        //将模板引擎内容解析成html字符串
        context.setVariable("email", email);
        String emailContent = templateEngine.process("verifySuccess", context);
        return emailContent;
    }
}

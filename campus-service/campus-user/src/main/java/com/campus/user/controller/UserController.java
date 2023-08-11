package com.campus.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.user.domain.User;
import com.campus.user.dto.CheckCodeForm;
import com.campus.user.dto.CheckEmailCodeForm;
import com.campus.user.dto.UpdatePasswordForm;
import com.campus.user.dto.UpdateUserForm;
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
import java.math.BigDecimal;
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

    @Value("${email.baseurl}")
    private String baseUrl;

    @ApiOperation(value = "获取用户信息")
    @GetMapping("/getDetail")
    public R getDetail(@RequestHeader("uid") String uid) {
        String userStr = String.valueOf(redisTemplate.opsForValue().get("user" + uid));
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
            return R.ok(user, "查询成功");
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
    public R updatePassword(@ApiParam("对象：包括password") @RequestBody UpdatePasswordForm form) {
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
        link = baseUrl + "/user/verifyEmail?email=" + email + "&userId=" + uid;

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
    public R sendEmail(@RequestBody Map<String, String> map) {

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
            return R.failed(null, "用户不存在");
        }
    }

    /**
     * 查看用户手机验证码是否正确
     */
    @ApiOperation(value = "查看用户验证码是否正确")
    @PostMapping("/checkPhoneCode")
    public R checkCode(@RequestBody CheckCodeForm form) {
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
    public R checkEmailCode(@RequestBody CheckEmailCodeForm form) {
        boolean b = userService.checkEmailCode(form);
        if (b) {
            return R.ok();
        } else {
            return R.failed();
        }
    }


    /**
     * 查询用户余额
     */
    @ApiOperation(value = "查询用户余额")
    @GetMapping("/getBalance/{userId}")
    public BigDecimal getBalance(@ApiParam("用户id") @PathVariable("userId")  String userId) {
        User user = userService.getById(userId);
        if (user != null) {
            BigDecimal balance = user.getBalance();
            return balance;
        } else {
            return null;
        }
    }

    /**
     * 修改用户余额
     */
    @ApiOperation(value = "修改用户余额")
    @PutMapping("/updateBalance/{userId}/{balance}")
    public R updateBalance(@ApiParam("用户id") @PathVariable("userId")  String userId, @PathVariable("balance") BigDecimal balance){
        return userService.updateBalance(userId, balance);

    }

}





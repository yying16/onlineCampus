package com.campus.user.controller;

import com.campus.common.util.R;
import com.campus.user.domain.User;
import com.campus.user.dto.LoginByCodeForm;
import com.campus.user.dto.LoginByEmailForm;
import com.campus.user.dto.LoginForm;
import com.campus.user.service.impl.UserServiceImpl;
import com.campus.user.util.EmailCodeUtil;
import com.campus.user.util.RandomUtil;
import com.campus.user.util.TokenUtil;
import com.campus.user.vo.LoginMessage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Api("登录相关接口")
public class LoginController {

    @Resource
    UserServiceImpl userService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;


    // 发送邮件的邮箱
    @Value("${spring.mail.username}")
    private String sendFrom;


    // 发送邮件的昵称
    @Value("${spring.mail.nickname}")
    private String nickname;

    @Autowired
    private JavaMailSender mailSender;

    //模板引擎
    @Autowired
    TemplateEngine templateEngine;

    /**
     * 登录按钮
     */
    @PostMapping("/login")
    @ApiOperation("用户名密码登录接口")
    public R login(@Valid @RequestBody LoginForm form) {
        LoginMessage message = userService.login(form);
        if(message==null){ // 账号或密码错误
            return R.failed(null,"验证码错误");
        }
        return R.ok(message);
    }


    @ApiOperation("手机号验证码登录接口")
    @PostMapping("/loginByCode")
    public R loginByCode(@Valid @RequestBody LoginByCodeForm form) {
        LoginMessage message = userService.login(form);
        if(message==null){ // 账号或密码错误
            return R.failed(null,"登录名和密码错误");
        }
        return R.ok(message);
    }


    /**
     * 发送短信验证码
     * */
    @ApiOperation("发送短信验证码")
    @GetMapping("send/{phone}")
    public R sendSms(@PathVariable String phone){

        //从redis获取验证码，如果获取到直接返回
        String code = redisTemplate.opsForValue().get(phone);
        log.info("redis中的验证码是："+code);
        if(StringUtils.hasLength(code)){
            return R.ok();
        }

        //生成随机值，传递给阿里云进行发送
        code = RandomUtil.getFourBitRandom();
        Map<String,Object> param = new HashMap<>();
        param.put("code",code);

        boolean isSend = userService.tencentSend(code,phone);

        if(isSend){
            //发送成功，把发送成功验证码放到redis里面
            //设置有效时间5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return R.ok(code,"发送短信成功");
        }else{
            return R.failed(null,"发送短信失败");
        }
    }


    /**
     * 退出登录
     * */
    @ApiOperation("退出登录")
    @GetMapping("logout")
    public R logout(HttpServletRequest request){
        //获取当前登录用户的token
        String token = request.getHeader("token");
        //从token中获取用户信息
        User user = TokenUtil.getClaimsFromToken(token);
        if (user == null) {
            return R.failed("token令牌无效");
        }else{
            log.info("退出登录的用户是："+user);
            //删除redis中的token
            redisTemplate.delete(user.getUserId());
            return R.ok();
        }
    }


    /**
     * 发送邮箱登录验证码
     */
    @ApiOperation("发送邮箱验证码")
    @GetMapping("sendEmail/{email}")
    public R sendEmail(@PathVariable String email) {
        //从redis获取验证码，如果获取到直接返回
        String code = redisTemplate.opsForValue().get(email);
        if (StringUtils.hasLength(code)) {
            return R.ok();
        }
        //生成随机验证码
        code = EmailCodeUtil.generateVerificationCode();

        //创建邮件上下文
        Context context = new Context();
        context.setVariable("verifyCode", Arrays.asList(code.split("")));

        //将模板引擎内容解析成html字符串
        String emailContent = templateEngine.process("emailTemplate", context);

        MimeMessage message=mailSender.createMimeMessage();

        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper=new MimeMessageHelper(message,true);
            helper.setFrom(nickname+'<'+sendFrom+'>');
            helper.setTo(email);
            helper.setSubject("校园服务平台-验证码");
            helper.setText(emailContent,true);
            mailSender.send(message);
            log.info("邮箱验证码是："+code);
            //发送成功，把发送成功验证码放到redis里面
            //设置有效时间5分钟
            redisTemplate.opsForValue().set(email,code,5, TimeUnit.MINUTES);
            return R.ok();
        }catch (MessagingException e) {
            e.printStackTrace();
            return R.failed(null,"发送邮件失败");
        }
    }


    /**
     * 邮箱登录
     */
    @ApiOperation("邮箱登录")
    @PostMapping("loginByEmail")
    public R loginByEmail(@Valid @RequestBody LoginByEmailForm form) {
        LoginMessage message = userService.login(form);
        if(message==null){ // 账号或密码错误
            return R.failed(null,"验证码错误");
        }
        return R.ok(message);
    }
}

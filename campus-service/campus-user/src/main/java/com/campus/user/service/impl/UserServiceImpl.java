package com.campus.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.AES;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.common.util.MD5;
import com.campus.common.util.R;
import com.campus.common.util.TimeUtil;
import com.campus.user.dao.UserDao;
import com.campus.user.domain.User;
import com.campus.user.dto.*;
import com.campus.user.feign.GatewayClient;
import com.campus.user.service.UserService;
import com.campus.user.util.AESVueUtil;
import com.campus.user.vo.LoginMessage;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@Log4j2
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {

    @Autowired
    StringRedisTemplate redisTemplate;


    @Autowired
    private JavaMailSender mailSender;

    // 发送邮件的邮箱
    @Value("${spring.mail.username}")
    private String sendFrom;


    // 发送邮件的昵称
    @Value("${spring.mail.nickname}")
    private String nickname;

    @Autowired
    GatewayClient gatewayClient;

    @Autowired
    UserDao userDao;

    final static int tokenCacheTime = 5; //token缓存时间 5小时（单位小时）

    /**
     * 登录并生成token
     *
     * @param form 登录表单
     * @return 登录消息（封装uid和token)
     */
    @Override
    public LoginMessage login(LoginForm form) {
        //md5加密后再查询/
        form.setPassword(MD5.encrypt(form.getPassword()));

        User user = userDao.getUser(form);
        if (user == null) { // 登录名和密码不匹配
            return null;
        }
        //登录成功
        JSONObject jsonUser = JSONObject.parseObject(JSONObject.toJSONString(user));
        String token = gatewayClient.generalToken(jsonUser);
        String uid = user.getUserId();
        return new LoginMessage(uid, token);
    }

    /**
     * 注册
     *
     * @param form
     */
    @Override
    public boolean register(RegisterForm form) {


        User user = new User();
        String id = IdWorker.getIdStr(user);
        user.setUserId(id);
        user.setAccount(form.getAccount());

        //md5加密
        String encryptPassword = MD5.encrypt(form.getPassword());

        //保存原始密码
        user.setOriginPassword(form.getPassword());

        user.setPassword(encryptPassword);
        user.setUsername(form.getUsername());
        user.setTelephone(form.getTelephone());
        user.setStatus(false);
        user.setDeleted(false);
        user.setCredit(60);
        user.setCreateTime(TimeUtil.getCurrentTime());
        user.setUpdateTime(TimeUtil.getCurrentTime());
        userDao.insert(user);
        //注册应该不直接登录把
//        JSONObject jsonUser = JSONObject.parseObject(JSONObject.toJSONString(user));
//        String token = gatewayClient.generalToken(jsonUser);// token缓存
        if (id != null) { // 数据写入成功
            return true;
        } else {
            return false;
        }
    }


    /**
     * 发送验证码
     */
    @Override
    public boolean tencentSend(String code, String phone) {
        try {
            // 实例化一个认证对象，入参需要传入腾讯云账户 SecretId 和 SecretKey，此处还需注意密钥对的保密
            // 代码泄露可能会导致 SecretId 和 SecretKey 泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议采用更安全的方式来使用密钥，请参见：https://cloud.tencent.com/document/product/1278/85305
            // 密钥可前往官网控制台 https://console.cloud.tencent.com/cam/capi 进行获取
            Credential cred = new Credential("AKIDOflfNyOdgX8apunaAYnrnW8dzGPN9i5T", "PC1e8QUWKvIQLAnCxWv9AxVpxrX0GI8k");
            // 实例化一个http选项，可选的，没有特殊需求可以跳过
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("sms.tencentcloudapi.com");
            // 实例化一个client选项，可选的，没有特殊需求可以跳过
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            // 实例化要请求产品的client对象,clientProfile是可选的
            SmsClient client = new SmsClient(cred, "ap-guangzhou", clientProfile);
            // 实例化一个请求对象,每个接口都会对应一个request对象
            SendSmsRequest req = new SendSmsRequest();
            String[] phoneNumberSet1 = {phone};
            req.setPhoneNumberSet(phoneNumberSet1);

            req.setSmsSdkAppId("1400852425");
                req.setSignName("财宝线上校园系统公众号");
            req.setTemplateId("1922871");
            String[] templateParamSet1 = {code};
            req.setTemplateParamSet(templateParamSet1);

            // 返回的resp是一个SendSmsResponse的实例，与请求对象对应
            SendSmsResponse resp = client.SendSms(req);
            // 输出json格式的字符串回包
            System.out.println(SendSmsResponse.toJsonString(resp));
            return true;
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
        return false;
    }

    /**
     * 根据手机号和验证码登录
     *
     * @param form
     */
    @Override
    public LoginMessage login(LoginByCodeForm form) {
        String telephone = form.getTelephone();
        String code = form.getCode();
        String authCode = redisTemplate.opsForValue().get(telephone + "_code");

        if (code.equals(authCode)) { // 验证码正确
            //验证码正确后，删除redis中的验证码
            redisTemplate.delete(telephone + "_code");
            User user = userDao.getUserByTelephone(telephone);
            JSONObject jsonUser = JSONObject.parseObject(JSONObject.toJSONString(user));
            String token = gatewayClient.generalToken(jsonUser);// token缓存
            String uid = user.getUserId();
            return new LoginMessage(uid, token);
        }
        return null;
    }

    @Override
    public LoginMessage login(LoginByEmailForm form) {
        String email = form.getEmail();
        String code = form.getCode();
        String authCode = redisTemplate.opsForValue().get(email+"_code");

        if (code.equals(authCode)) { // 验证码正确
            //验证码正确后，删除redis中的验证码
            redisTemplate.delete(email+"_code");

            User user = userDao.getUserByEmail(email);
            JSONObject jsonUser = JSONObject.parseObject(JSONObject.toJSONString(user));
            String token = gatewayClient.generalToken(jsonUser);// token缓存
            String uid = user.getUserId();
            return new LoginMessage(uid, token);
        }
        return null;
    }

    @Override
    public boolean updatePassword(UpdatePasswordForm form) {
        String encryptedAccount = form.getAccount();
        String encryptedPassword = form.getPassword();
        // 唯一key作为密钥
        String uniqueKey = "S0JsiZY2eHlgnRmv";

        log.info("encryptedAccount: " + encryptedAccount);
        log.info("encryptedPassword: " + encryptedPassword);

        // 解密
        String account = null;
        String password = null;
        try {
            account = AESVueUtil.decrypt(encryptedAccount, uniqueKey);
            password = AESVueUtil.decrypt(encryptedPassword, uniqueKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("account: " + account);
        log.info("password: " + password);

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("account", account);
        User user = userDao.selectOne(queryWrapper);

        if (user == null) {
            return false;
        }
        //md5加密，更新密码
        user.setPassword(MD5.encrypt(password));
        userDao.updateById(user);
        return true;
    }

    /**
     * 发送邮件
     *
     * @param emailContent
     * @param email
     * @return
     */
    @Override
    public boolean sendEmail(String emailContent, String email) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            //true表示需要创建一个multipart message
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(nickname + '<' + sendFrom + '>');
            helper.setTo(email);
            helper.setSubject("校园服务平台-通知邮件");
            helper.setText(emailContent, true);
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        return userDao.selectOne(queryWrapper);
    }

    @Override
    public void activateEmail(String email, String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        User user = userDao.selectOne(queryWrapper);
        user.setEmail(email);
        userDao.updateById(user);
    }

    /**
     * 数据校验-账号
     *
     * @param account
     */
    @Override
    public boolean checkAccountHasRegister(String account) {
        try {
            //根据account查询用户
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("account", account);

            return userDao.selectOne(wrapper) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 数据校验-手机号
     *
     * @param telephone
     */
    @Override
    public boolean checkTelephoneHasRegister(String telephone) {
        try {
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq("telephone", telephone);
            return userDao.selectOne(wrapper) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查验证码是否正确
     *
     * @param form
     * @return
     */
    @Override
    public boolean checkCode(CheckCodeForm form) {
        String telephone = form.getTelephone();
        String code = form.getCode();
        String authCode = redisTemplate.opsForValue().get(telephone + "_code");
        if (code.equals(authCode)) { // 验证码正确
            //验证过后删除验证码
            redisTemplate.delete(telephone + "_code");
            return true;
        }
        return false;
    }

    @Override
    public boolean checkEmailCode(CheckEmailCodeForm form) {
        String email = form.getEmail();
        String code = form.getCode();
        //忽略大小写
        code = code.toLowerCase();
        String authCode = redisTemplate.opsForValue().get(email+ "_code");
        authCode = authCode.toLowerCase();
        if (code.equals(authCode)) { // 验证码正确
            //验证过后删除验证码
            redisTemplate.delete(email+ "_code");
            return true;
        }
        return false;
    }

    @Override
    public R updateBalance(String userId, BigDecimal balance) {
        User user = userDao.selectById(userId);
        if (user != null) {
            user.setBalance(balance);
            int i = userDao.updateById(user);
            if (i > 0) {

                return R.ok(null, "修改成功");
            } else {
                return R.failed(null, "修改失败");
            }
        } else {
            return R.failed(null, "用户不存在");
        }
    }

    /**
     * 获取未认证的用户列表
     * @return
     */
    @Override
    public List<User> getUserWithNotAuth(Integer auth) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(auth != null,User::getAuth,auth);
        List<User> userList = userDao.selectList(wrapper);
        return userList;
    }


//    /**
//     * 获取用户详细信息
//     *
//     * @param uid 用户id
//     */
//    @Override
//    public User getDetail(String uid) {
//        String json = redisTemplate.opsForValue().get(uid);
//        JSONObject jsonObject = JSONObject.parseObject(json);
//        if (jsonObject.get("data") != null && String.valueOf(jsonObject.get("data")).length() > 0) {
//            User user = JSONObject.parseObject(String.valueOf(jsonObject.get("data")), User.class);
//            return user;
//        }
//        return null;
//    }
}

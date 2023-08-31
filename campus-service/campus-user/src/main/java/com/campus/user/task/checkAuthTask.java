package com.campus.user.task;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.R;
import com.campus.user.domain.User;
import com.campus.user.feign.MessageClient;
import com.campus.user.pojo.PromptInformationForm;
import com.campus.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: chb
 * @Date: 2023/08/25/17:25
 * @Description:
 */
@Slf4j
@Component
public class checkAuthTask {

    @Autowired
    private ServiceCenter serviceCenter;

    @Autowired
    private UserService userService;
    
    @Autowired
    private MessageClient messageClient;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkAuth(){
        log.info("******检查并用户是否认证定时器开始******");
        try {
            //获取未认证的用户
            List<User> userList = userService.getUserWithNotAuth(0);
            if (userList == null){
                return;
            }
            List<String> idList = userList.stream().map(User::getUserId).collect(Collectors.toList());
            for (String id : idList) {
                R online = messageClient.isOnline(id);
                int code = online.getCode();
                if (code == 0) {
                    //发送系统消息
                    PromptInformationForm form = new PromptInformationForm();
                    form.setContent("请您完善个人认证信息");
                    for (User user : userList) {
                        form.setReceiver(user.getUserId());
                        messageClient.sendPromptInformation(form);
                    }
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(checkAuthTask.class).error("认证定时器出现异常"+ "-------->" +e.toString());
        }
        log.info("******检查并用户是否认证定时器开始******");
    }
}

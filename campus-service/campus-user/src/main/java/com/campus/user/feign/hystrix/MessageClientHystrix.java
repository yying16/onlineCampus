package com.campus.user.feign.hystrix;

import com.campus.common.util.R;
import com.campus.user.feign.MessageClient;
import com.campus.user.pojo.PromptInformationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageClientHystrix implements MessageClient {

    @Override
    public R sendPromptInformation(PromptInformationForm form) {
        return R.failed();
    }

    /**
     * 登录时用户初始化信息
     *
     * @param uid
     */
    @Override
    public R initMessage(String uid) {
        return R.failed();
    }

    /**
     * 退出登录时清除用户消息缓存区
     *
     * @param uid
     */
    @Override
    public R clearCache(String uid) {
        return null;
    }
}

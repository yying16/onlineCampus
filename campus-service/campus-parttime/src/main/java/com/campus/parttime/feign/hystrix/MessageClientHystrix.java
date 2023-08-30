package com.campus.parttime.feign.hystrix;

import com.campus.common.util.R;
import com.campus.parttime.feign.MessageClient;
import com.campus.parttime.pojo.HandleRequestForm;
import com.campus.parttime.pojo.MessageForm;
import com.campus.parttime.pojo.PromptInformationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author kakakaka
 */

@Slf4j
@Component

public class MessageClientHystrix implements MessageClient {

    @Override
    public R send(MessageForm form) {
        return R.failed();
    }

    @Override
    public R handleRequest(HandleRequestForm form, String uid) {
        return R.failed();
    }

    @Override
    public R sendPromptInformation(PromptInformationForm form) {
        return R.failed();
    }

}

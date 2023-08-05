package com.campus.parttime.feign.hystrix;

import com.campus.common.util.R;
import com.campus.message.dto.HandleRequestForm;
import com.campus.message.dto.MessageForm;
import com.campus.message.dto.PromptInformationForm;
import com.campus.parttime.feign.MessageClient;
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

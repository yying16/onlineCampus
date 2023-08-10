package com.campus.parttime.feign;

import com.campus.parttime.feign.hystrix.MessageClientHystrix;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * author kakakaka
 */


@Component
public class MessageFeignFactory implements FallbackFactory<MessageClient> {

    private final MessageClientHystrix messageClientHystrix;

    public MessageFeignFactory(MessageClientHystrix messageClientHystrix) {
        this.messageClientHystrix = messageClientHystrix;
    }

    @Override
    public MessageClient create(Throwable throwable) {
        throwable.printStackTrace();
        return messageClientHystrix;
    }
}

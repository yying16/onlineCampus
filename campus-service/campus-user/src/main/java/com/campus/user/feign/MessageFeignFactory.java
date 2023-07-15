package com.campus.user.feign;


import com.campus.user.feign.hystrix.MessageClientHystrix;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

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
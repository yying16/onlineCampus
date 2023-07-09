package com.campus.message.feign;

import com.campus.message.feign.hystrix.UserClientHystrix;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class UserFeignFactory implements FallbackFactory<UserClient> {

    private final UserClientHystrix userClientHystrix;

    public UserFeignFactory(UserClientHystrix messageClientHystrix) {
        this.userClientHystrix = messageClientHystrix;
    }

    @Override
    public UserClient create(Throwable throwable) {
        throwable.printStackTrace();
        return userClientHystrix;
    }
}
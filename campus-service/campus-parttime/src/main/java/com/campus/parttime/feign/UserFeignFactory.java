package com.campus.parttime.feign;

import com.campus.parttime.feign.hystrix.UserClientHystrix;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * author kakakaka
 */
@Component
public class UserFeignFactory  implements FallbackFactory<UserClient> {

    private final UserClientHystrix userClientHystrix;

    public UserFeignFactory(UserClientHystrix userClientHystrix) {
        this.userClientHystrix = userClientHystrix;
    }

    @Override
    public UserClient create(Throwable throwable) {
        throwable.printStackTrace();
        return userClientHystrix;
    }
}

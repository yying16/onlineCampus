package com.campus.parttime.feign;

import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * author kakakaka
 */

public class UserFeignFactory  implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return null;
    }
}

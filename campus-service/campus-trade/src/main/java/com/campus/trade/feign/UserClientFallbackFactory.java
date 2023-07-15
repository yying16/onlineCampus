package com.campus.trade.feign;

import com.campus.common.util.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * @auther xiaolin
 * @create 2023/7/13 11:29
 */
@Component
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {

    private static final Logger logger = LoggerFactory.getLogger(UserClientFallbackFactory.class);

    @Override
    public UserClient create(Throwable cause) {
        //一进入异常就能知道什么异常
        UserClientFallbackFactory.logger.info("fallback; exception was: {}", cause.toString());
        UserClientFallbackFactory.logger.info("fallback; reason was: {}", cause.getMessage());

        return new UserClient() {
            @Override
            public R getUserById(String userId) {
                return R.failed();
            }
        };
    }
}

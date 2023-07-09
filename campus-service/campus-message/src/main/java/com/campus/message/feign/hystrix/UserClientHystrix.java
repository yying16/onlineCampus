package com.campus.message.feign.hystrix;

import com.campus.common.util.R;
import com.campus.message.feign.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class UserClientHystrix implements UserClient {

    @Override
    public R getAllUserId() {
        log.info("getAllUserId error ! ");
        List<String> list = new ArrayList<>();
        return R.ok(list);
    }

    @Override
    public R getAutoReply(String userId) {
        return R.failed();
    }
}

package com.campus.parttime.feign.hystrix;

import com.campus.common.util.R;
import com.campus.parttime.feign.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * author kakakaka
 */

@Slf4j
@Component
public class UserClientHystrix implements UserClient {
    @Override
    public R getUserById(String userId){ return R.failed();}
}

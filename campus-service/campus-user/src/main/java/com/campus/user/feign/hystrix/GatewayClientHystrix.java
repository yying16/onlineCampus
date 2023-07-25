package com.campus.user.feign.hystrix;

import com.alibaba.fastjson.JSONObject;
import com.campus.common.util.R;
import com.campus.user.feign.GatewayClient;
import com.campus.user.feign.MessageClient;
import com.campus.user.pojo.PromptInformationForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GatewayClientHystrix implements GatewayClient {

    /**
     * 后面再写策略
     * */
    @Override
    public String generalToken(JSONObject user) {
        return null;
    }
}

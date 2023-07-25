package com.campus.user.feign;


import com.campus.user.feign.hystrix.GatewayClientHystrix;
import com.campus.user.feign.hystrix.MessageClientHystrix;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class GatewayFeignFactory implements FallbackFactory<GatewayClient> {

    private final GatewayClientHystrix gatewayClientHystrix;

    public GatewayFeignFactory(GatewayClientHystrix gatewayClientHystrix) {
        this.gatewayClientHystrix = gatewayClientHystrix;
    }

    @Override
    public GatewayClient create(Throwable throwable) {
        throwable.printStackTrace();
        return gatewayClientHystrix;
    }
}
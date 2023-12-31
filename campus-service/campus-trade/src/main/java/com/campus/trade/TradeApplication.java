package com.campus.trade;

import com.campus.common.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @auther xiaolin
 * @create 2023/7/9 10:48
 */
@SpringBootApplication(scanBasePackages = {"com.campus"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableAsync
@EnableScheduling
public class TradeApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TradeApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}

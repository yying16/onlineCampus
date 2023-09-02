package com.campus.user;

import com.campus.common.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.user"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableAsync
public class UserApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UserApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}

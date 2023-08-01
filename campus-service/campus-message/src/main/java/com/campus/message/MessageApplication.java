package com.campus.message;

import com.campus.common.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.message"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@EnableAsync
public class MessageApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MessageApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}

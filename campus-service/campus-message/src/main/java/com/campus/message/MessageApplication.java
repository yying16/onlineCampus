package com.campus.message;

import com.campus.common.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.message"})
@EnableFeignClients
@EnableDiscoveryClient
public class MessageApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MessageApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}

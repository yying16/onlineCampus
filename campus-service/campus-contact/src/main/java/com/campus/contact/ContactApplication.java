package com.campus.contact;

import com.campus.common.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.contact"})
@EnableDiscoveryClient
@EnableAsync
public class ContactApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ContactApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}

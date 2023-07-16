package com.campus.parttime;

import com.campus.common.util.SpringContextUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.parttime"})
@EnableFeignClients
@EnableDiscoveryClient
public class CampusParttimeApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CampusParttimeApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

}

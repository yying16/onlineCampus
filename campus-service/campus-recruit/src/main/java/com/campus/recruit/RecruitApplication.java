package com.campus.recruit;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.SpringContextUtil;
import com.campus.recruit.domain.Recruit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.recruit"})
@EnableFeignClients
@EnableDiscoveryClient
public class RecruitApplication {

    @Autowired
    ServiceCenter serviceCenter;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(RecruitApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

    @Bean
    public CommandLineRunner CommandLineRunner(){
        return args-> serviceCenter.registerTask(Recruit.class);
    }

}

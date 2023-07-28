package com.campus.parttime;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.SpringContextUtil;
import com.campus.parttime.domain.Job;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.parttime"})
@EnableDiscoveryClient
public class CampusParttimeApplication {
    @Autowired
    ServiceCenter serviceCenter;

    public static void main(String[] args){
        ConfigurableApplicationContext context = SpringApplication.run(CampusParttimeApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

    @Bean
    public CommandLineRunner CommandLineRunner(){
        return args->{
            serviceCenter.registerTask(Job.class);
        };
    }
}

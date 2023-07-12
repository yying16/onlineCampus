package com.campus.test;

import com.campus.common.service.ServiceCenter;
import com.campus.common.util.SpringContextUtil;
import com.campus.test.domain.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.test"})
public class TestApplication {

    @Autowired
    ServiceCenter serviceCenter;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestApplication.class, args);
        SpringContextUtil.setApplicationContext(context);

    }

    @Bean
    public CommandLineRunner CommandLineRunner(){
        return args->{
            serviceCenter.registerTask(Test.class);
        };
    }
}

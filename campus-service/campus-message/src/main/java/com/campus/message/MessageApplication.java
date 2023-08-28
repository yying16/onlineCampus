package com.campus.message;

import com.campus.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.message"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@EnableAsync
@Slf4j
public class MessageApplication {

    @Value("${serverId}")
    String serverId;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MessageApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            log.info("【服务id】 {}",serverId);
        };
    }
}

package com.campus.contact;

import com.campus.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.InetAddress;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.contact"})
@EnableDiscoveryClient
@EnableAsync
@Slf4j
public class ContactApplication {

    @Value("${server.port}")
    String serverPort;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(ContactApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            InetAddress inetAddress = InetAddress.getLocalHost();
            log.info("【当前ip和端口号】 {} : {}",inetAddress.getHostAddress(),serverPort);
        };
    }
}

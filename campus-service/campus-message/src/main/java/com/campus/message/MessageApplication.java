package com.campus.message;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.campus.common.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.net.InetAddress;
import java.util.Properties;

@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.message"})
@EnableFeignClients
@EnableDiscoveryClient
@EnableScheduling
@EnableAsync
@Slf4j
public class MessageApplication {

    @Value("${server.port}")
    String serverPort;

    @Value("${serverId}")
    String serverId;

    @Autowired
    ConfigService configService;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MessageApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }

    @Bean
    CommandLineRunner commandLineRunner() {
        return args -> {
            InetAddress inetAddress = InetAddress.getLocalHost();
            log.info("【当前ip和端口号】 {} : {}",inetAddress.getHostAddress(),serverPort);
            log.info("【serverId】:{}",serverId);
        };
    }
}

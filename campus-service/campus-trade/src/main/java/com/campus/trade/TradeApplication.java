package com.campus.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @auther xiaolin
 * @create 2023/7/9 10:48
 */
@SpringBootApplication(scanBasePackages = {"com.campus"})
@EnableFeignClients
@EnableDiscoveryClient
public class TradeApplication {
    public static void main(String[] args) {

        SpringApplication.run(TradeApplication.class, args);
    }
}

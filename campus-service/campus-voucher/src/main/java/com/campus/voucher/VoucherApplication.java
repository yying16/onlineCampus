package com.campus.voucher;

import com.campus.common.util.SpringContextUtil;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: chb
 * @Date: 2023/09/06/20:03
 * @Description:
 */
@MapperScan(value = "com.campus.voucher.dao")
@SpringBootApplication(scanBasePackages = {"com.campus.common","com.campus.voucher"})
@EnableDiscoveryClient
@EnableFeignClients
@EnableAsync
@EnableConfigurationProperties
public class VoucherApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VoucherApplication.class, args);
        SpringContextUtil.setApplicationContext(context);
    }
}

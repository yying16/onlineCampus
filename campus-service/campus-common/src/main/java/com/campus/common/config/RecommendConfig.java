package com.campus.common.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class RecommendConfig {

    @Value("${spring.datasource.druid.url}")
    String url;

    @Value("${spring.datasource.druid.driverClassName}")
    String driverClassName;

    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    String password;

    @Value("${spring.datasource.druid.initial-size}")
    Integer initialSize;

    @Value("${spring.datasource.druid.min-idle}")
    Integer minIdle;


    /**
     * 初始化连接数据库DataSource
     * */
    @Bean
    public DataSource campusDataSource(){
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url); // 替换为你的MySQL数据库URL和数据库名
        dataSource.setUsername(username); // 替换为你的MySQL数据库用户名
        dataSource.setPassword(password); // 替换为你的MySQL数据库密码
        dataSource.setInitialSize(initialSize); // 初始连接池大小
        dataSource.setMinIdle(minIdle); // 最小空闲连接数
        return dataSource;
    }

}

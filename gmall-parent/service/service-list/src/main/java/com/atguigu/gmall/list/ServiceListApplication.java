package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Miluo
 * @description 启动类
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class,scanBasePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
@EnableFeignClients
public class ServiceListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceListApplication.class,args);
    }
}

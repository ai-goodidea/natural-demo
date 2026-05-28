package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(scanBasePackages = {"org.example.business", "org.example.common", "org.example.feign.fallback"})
@EnableDiscoveryClient
@EnableAsync(proxyTargetClass = true)
@EnableFeignClients(basePackages = "org.example.feign.client")
@MapperScan("org.example.business.mapper")
public class BusinessApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessApplication.class, args);
    }
}

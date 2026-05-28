package org.example;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = {"org.example.supplier", "org.example.common"})
@EnableDiscoveryClient
@MapperScan("org.example.supplier.mapper")
public class SupplierApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupplierApplication.class, args);
    }
}

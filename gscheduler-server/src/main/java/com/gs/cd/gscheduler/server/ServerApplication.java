package com.gs.cd.gscheduler.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author seven
 * @Date 2021/4/13 17:03
 * @Description
 * @Version 1.0
 */
@SpringBootApplication
@EnableFeignClients(basePackages = "com.gs.cd.gscheduler.api")
@MapperScan("com.gs.cd.gscheduler.server.mapper")
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}

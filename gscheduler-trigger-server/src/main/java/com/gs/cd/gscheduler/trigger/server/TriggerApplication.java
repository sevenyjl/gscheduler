package com.gs.cd.gscheduler.trigger.server;

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
@EnableFeignClients
@MapperScan("com.gs.cd.gscheduler.trigger.server.mapper")
public class TriggerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TriggerApplication.class, args);
    }
}

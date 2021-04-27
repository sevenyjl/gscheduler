package com.gs.cd.gscheduler.server;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author seven
 * @Date 2021/4/27 14:35
 * @Description
 * @Version 1.0
 */
@SpringBootApplication
@EnableFeignClients
@MapperScan("com.gs.cd.gscheduler.dao.mapper")
public class GschedulerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GschedulerServerApplication.class, args);
    }
}

package com.gs.cd.gscheduler.api;

import com.gs.cd.db.dynamic.core.register.DynamicDataSourceRegister;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

/**
 * @Author seven
 * @Date 2021/4/27 14:35
 * @Description
 * @Version 1.0
 */
@SpringBootApplication(scanBasePackages = {"com.gs.cd.gscheduler.api","com.gs.cd.db.dynamic.*"})
@Import(DynamicDataSourceRegister.class)
@EnableFeignClients
@MapperScan("com.gs.cd.gscheduler.dao.mapper")
public class GschedulerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GschedulerServerApplication.class, args);
    }
}

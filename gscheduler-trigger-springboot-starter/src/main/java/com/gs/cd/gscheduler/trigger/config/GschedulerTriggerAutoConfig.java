package com.gs.cd.gscheduler.trigger.config;

import com.gs.cd.gscheduler.trigger.openfeign.TriggerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author seven
 * @Date 2021/4/27 16:08
 * @Description
 * @Version 1.0
 */
@Configuration
@EnableFeignClients(clients = {TriggerClient.class})
@EnableConfigurationProperties
@Slf4j
public class GschedulerTriggerAutoConfig {
    public GschedulerTriggerAutoConfig() {
        log.info("****** GSchedulerTrigger init Success ************");
    }

}

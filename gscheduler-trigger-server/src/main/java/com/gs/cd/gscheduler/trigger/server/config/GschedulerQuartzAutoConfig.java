package com.gs.cd.gscheduler.trigger.server.config;

import com.gs.cd.gscheduler.trigger.server.quartz.QuartzExecutors;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author seven
 * @Date 2021/4/27 16:08
 * @Description
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties
@Slf4j
public class GschedulerQuartzAutoConfig {
    public GschedulerQuartzAutoConfig() {
    }

    @Bean
    @ConditionalOnMissingBean(QuartzExecutors.class)
    public QuartzExecutors quartzExecutors() throws SchedulerException {
        try {
            return new QuartzExecutors();
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw e;
        }
    }

}

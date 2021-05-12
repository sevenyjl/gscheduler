package com.gs.cd.gscheduler.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * @Author seven
 * @Date 2021/5/11 18:50
 * @Description
 * @Version 1.0
 */
@Component
public class InitTriggerScheduler implements ApplicationRunner {
    @Autowired

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("init trigger");
    }

    @PreDestroy
    public void destory() {
        System.out.println("我被销毁了、、、、、我是用的@PreDestory的方式、、、、、、");
        System.out.println("我被销毁了、、、、、我是用的@PreDestory的方式、、、、、、");
    }
}

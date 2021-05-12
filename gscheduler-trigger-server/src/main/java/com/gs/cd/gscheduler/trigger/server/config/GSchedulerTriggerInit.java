package com.gs.cd.gscheduler.trigger.server.config;

import com.gs.cd.gscheduler.trigger.server.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.trigger.server.quartz.QuartzExecutors;
import com.gs.cd.gscheduler.trigger.server.service.GschedulerTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/5/12 13:38
 * @Description
 * @Version 1.0
 */
@Component
@Slf4j
public class GSchedulerTriggerInit implements ApplicationRunner {
    @Autowired
    private GschedulerTriggerService gschedulerTriggerService;
    @Autowired
    private QuartzExecutors quartzExecutors;
    private static List<GschedulerTrigger> temp = new ArrayList<>();

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("*** init trigger ***");
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化失败:{}", e.getMessage());
            System.exit(-1);
        }
    }

    public void init() {
        List<GschedulerTrigger> list = gschedulerTriggerService.list();
        list.forEach(s -> {
            if (!s.getLockFlag()) {
                quartzExecutors.addJob(s);
                temp.add(s);
                log.debug("创建定时任务：{}", s);
            }
        });
        gschedulerTriggerService.lockBathById(temp);
        log.info("初始化{}条定时任务", temp.size());
    }

    @PreDestroy
    public void destory() {
        log.info("消除定时任务--->");
        gschedulerTriggerService.unlockBathById(temp);
    }
}
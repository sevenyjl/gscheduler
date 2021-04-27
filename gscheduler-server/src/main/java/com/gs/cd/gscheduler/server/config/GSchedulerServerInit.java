package com.gs.cd.gscheduler.server.config;

import com.gs.cd.gscheduler.dao.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.server.quartz.QuartzExecutors;
import com.gs.cd.gscheduler.server.service.impl.GschedulerTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class GSchedulerServerInit implements ApplicationRunner {

    @Autowired
    GschedulerTriggerService gschedulerTriggerService;
    @Autowired
    QuartzExecutors quartzExecutors;

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        TriggerInit();

    }

    public void TriggerInit() {
        log.info("*******\tinit GSchedulerTrigger corn task\t*******");
        List<GschedulerTrigger> list = gschedulerTriggerService.list();
        list.forEach(t -> quartzExecutors.addJob(t));
    }

}
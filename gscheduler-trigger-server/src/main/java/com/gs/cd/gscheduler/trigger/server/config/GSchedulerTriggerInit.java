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
 * todo 关于表的行锁 建议记录一下ip等信息 方便追踪问题定位
 */
@Component
@Slf4j
public class GSchedulerTriggerInit implements ApplicationRunner {
    @Autowired
    private GschedulerTriggerService gschedulerTriggerService;
    @Autowired
    private QuartzExecutors quartzExecutors;
    private static List<GschedulerTrigger> cache = new ArrayList<>();

    public static void addCache(GschedulerTrigger gschedulerTrigger) {
        cache.add(gschedulerTrigger);
    }

    private String beautifyLog(String s) {
        return "===\t" + s + "\n";
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("\n\n");
        System.out.println("*** init trigger ***");
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("初始化失败:{}", e.getMessage());
            System.exit(-1);
        }
        System.out.println("\n\n");
    }

    public void init() {
        List<GschedulerTrigger> list = gschedulerTriggerService.list();
        list.forEach(s -> {
            if (!s.getLockFlag()) {
                s.params2ITrigger();
                quartzExecutors.addJob(s);
                cache.add(s);
                log.debug("创建定时任务：{}", s);
            }
        });
        gschedulerTriggerService.lockBathById(cache);
        System.out.printf(beautifyLog("初始化%s条定时任务"), cache.size());
    }

    @PreDestroy
    // TODO: 2021/5/13 将定时任务给下一个服务
    public void destory() {
        System.out.println("\n\n");
        System.out.printf(beautifyLog("消除定时任务: %s条"), cache.size());
        gschedulerTriggerService.unlockBathById(cache);
        System.out.println("\n\n");
    }
}
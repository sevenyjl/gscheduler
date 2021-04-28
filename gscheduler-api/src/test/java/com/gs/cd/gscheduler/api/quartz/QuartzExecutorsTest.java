package com.gs.cd.gscheduler.api.quartz;

import com.gs.cd.gscheduler.quartz.QuartzExecutors;
import com.gs.cd.gscheduler.api.GschedulerServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author seven
 * @Date 2021/4/27 17:16
 * @Description
 * @Version 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GschedulerServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class QuartzExecutorsTest {

    @Autowired
    QuartzExecutors quartzExecutors;

    @Test
    public void test() throws InterruptedException {
//        for (int i = 0; i < 100; i++) {
//            quartzExecutors.addJob(GschedulerTriggerJob.class, "jobname", "group", new Date()
//                    , new Date(System.currentTimeMillis() + 10000), "* * * * * ? *", Map.of("data", i));
//            quartzExecutors.addJob(GschedulerTriggerJob.class, "jobname-" + i, "group", new Date()
//                    , new Date(System.currentTimeMillis() + 10000), "* * * * * ? *", Map.of("data", i));
//        }
//        Thread.sleep(10000L);
    }
}
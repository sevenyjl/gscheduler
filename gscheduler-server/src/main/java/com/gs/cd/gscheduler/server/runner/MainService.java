package com.gs.cd.gscheduler.server.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author seven
 * @Date 2021/5/6 16:49
 * @Description
 * @Version 1.0
 */
@Service
public class MainService extends Thread {

    /**
     * logger of MasterSchedulerThread
     */
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);


    /**
     * master exec service
     */
    private ThreadPoolExecutor masterExecService;


    /**
     * constructor of MasterSchedulerThread
     */
    @PostConstruct
    public void init() {
    }

    @Override
    public void start() {
        super.setName("MasterSchedulerThread");
        super.start();
    }

    public void close() {
        masterExecService.shutdown();
        boolean terminated = false;
        try {
            terminated = masterExecService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {
        }
        if (!terminated) {
            logger.warn("masterExecService shutdown without terminated, increase await time");
        }
        logger.info("master schedule service stopped...");
    }

    /**
     * run of MasterSchedulerThread
     */
    @Override
    public void run() {
        logger.info("master scheduler started");
        // TODO: 2021/5/6 轮询监听 command 表并获取最新数据
        // 如果有 command
//            判断command 是否需要创建实例
        // 如果没有 休眠
    }

}

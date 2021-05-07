/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.cd.gscheduler.server.master.runner;

import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.thread.Stopper;
import com.gs.cd.gscheduler.common.thread.ThreadUtils;
import com.gs.cd.gscheduler.common.utils.OSUtils;
import com.gs.cd.gscheduler.dao.entity.Command;
import com.gs.cd.gscheduler.dao.entity.ProcessInstance;
import com.gs.cd.gscheduler.remote.NettyRemotingClient;
import com.gs.cd.gscheduler.remote.config.NettyClientConfig;
import com.gs.cd.gscheduler.server.master.config.MasterConfig;
import com.gs.cd.gscheduler.server.zk.ZKMasterClient;
import com.gs.cd.gscheduler.service.process.ProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *  master scheduler thread
 */
@Service
public class MasterSchedulerService extends Thread {

    /**
     * logger of MasterSchedulerThread
     */
    private static final Logger logger = LoggerFactory.getLogger(MasterSchedulerService.class);

    /**
     * gscheduler database interface
     */
    @Autowired
    private ProcessService processService;

    /**
     * zookeeper master client
     */
    @Autowired
    private ZKMasterClient zkMasterClient;

    /**
     * master config
     */
    @Autowired
    private MasterConfig masterConfig;

    /**
     *  netty remoting client
     */
    private NettyRemotingClient nettyRemotingClient;

    /**
     * master exec service
     */
    private ThreadPoolExecutor masterExecService;


    /**
     * constructor of MasterSchedulerThread
     */
    @PostConstruct
    public void init(){
        this.masterExecService = (ThreadPoolExecutor)ThreadUtils.newDaemonFixedThreadExecutor("Master-Exec-Thread", masterConfig.getMasterExecThreads());
        NettyClientConfig clientConfig = new NettyClientConfig();
        this.nettyRemotingClient = new NettyRemotingClient(clientConfig);
    }

    @Override
    public void start(){
        super.setName("MasterSchedulerThread");
        super.start();
    }

    public void close() {
        masterExecService.shutdown();
        boolean terminated = false;
        try {
            terminated = masterExecService.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException ignore) {}
        if(!terminated){
            logger.warn("masterExecService shutdown without terminated, increase await time");
        }
        nettyRemotingClient.close();
        logger.info("master schedule service stopped...");
    }

    /**
     * run of MasterSchedulerThread
     */
    @Override
    public void run() {
        logger.info("master scheduler started");
        while (Stopper.isRunning()){
            InterProcessMutex mutex = null;
            try {
                boolean runCheckFlag = OSUtils.checkResource(masterConfig.getMasterMaxCpuloadAvg(), masterConfig.getMasterReservedMemory());
                if(!runCheckFlag) {
                    Thread.sleep(Constants.SLEEP_TIME_MILLIS);
                    continue;
                }
                if (zkMasterClient.getZkClient().getState() == CuratorFrameworkState.STARTED) {

                    mutex = zkMasterClient.blockAcquireMutex();

                    int activeCount = masterExecService.getActiveCount();
                    // make sure to scan and delete command  table in one transaction
                    Command command = processService.findOneCommand();
                    if (command != null) {
                        logger.info("find one command: id: {}, type: {}", command.getId(),command.getCommandType());

                        try{

                            ProcessInstance processInstance = processService.handleCommand(logger,
                                    getLocalAddress(),
                                    this.masterConfig.getMasterExecThreads() - activeCount, command);
                            if (processInstance != null) {
                                logger.info("start master exec thread , split DAG ...");
                                masterExecService.execute(new MasterExecThread(processInstance, processService, nettyRemotingClient));
                            }
                        }catch (Exception e){
                            logger.error("scan command error ", e);
                            processService.moveToErrorCommand(command, e.toString());
                        }
                    } else{
                        //indicate that no command ,sleep for 1s
                        Thread.sleep(Constants.SLEEP_TIME_MILLIS);
                    }
                }
            } catch (Exception e){
                logger.error("master scheduler thread error",e);
            } finally{
                zkMasterClient.releaseMutex(mutex);
            }
        }
    }

    private String getLocalAddress(){
        return OSUtils.getHost() + ":" + masterConfig.getListenPort();
    }
}

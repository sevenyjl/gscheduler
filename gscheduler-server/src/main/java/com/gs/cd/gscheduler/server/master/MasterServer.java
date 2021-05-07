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
package com.gs.cd.gscheduler.server.master;

import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.thread.Stopper;
import com.gs.cd.gscheduler.server.master.config.MasterConfig;
import com.gs.cd.gscheduler.server.master.processor.TaskAckProcessor;
import com.gs.cd.gscheduler.server.master.processor.TaskKillResponseProcessor;
import com.gs.cd.gscheduler.server.master.processor.TaskResponseProcessor;
import com.gs.cd.gscheduler.server.master.registry.MasterRegistry;
import com.gs.cd.gscheduler.server.master.runner.MasterSchedulerService;
import com.gs.cd.gscheduler.remote.NettyRemotingServer;
import com.gs.cd.gscheduler.remote.command.CommandType;
import com.gs.cd.gscheduler.remote.config.NettyServerConfig;
import com.gs.cd.gscheduler.server.worker.WorkerServer;
import com.gs.cd.gscheduler.server.zk.ZKMasterClient;
import com.gs.cd.gscheduler.service.bean.SpringApplicationContext;
import com.gs.cd.gscheduler.service.quartz.QuartzExecutors;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import javax.annotation.PostConstruct;



@ComponentScan(value = "com.gs.cd.gscheduler", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WorkerServer.class})
})
public class MasterServer {

    /**
     * logger of MasterServer
     */
    private static final Logger logger = LoggerFactory.getLogger(MasterServer.class);

    /**
     * master config
     */
    @Autowired
    private MasterConfig masterConfig;

    /**
     *  spring application context
     *  only use it for initialization
     */
    @Autowired
    private SpringApplicationContext springApplicationContext;

    /**
     * netty remote server
     */
    private NettyRemotingServer nettyRemotingServer;

    /**
     * master registry
     */
    @Autowired
    private MasterRegistry masterRegistry;

    /**
     * zk master client
     */
    @Autowired
    private ZKMasterClient zkMasterClient;

    /**
     * scheduler service
     */
    @Autowired
    private MasterSchedulerService masterSchedulerService;

    /**
     * master server startup
     *
     * master server not use web service
     * @param args arguments
     */
    public static void main(String[] args) {
        Thread.currentThread().setName(Constants.THREAD_NAME_MASTER_SERVER);
        new SpringApplicationBuilder(MasterServer.class).web(WebApplicationType.NONE).run(args);
    }

    /**
     * run master server
     */
    @PostConstruct
    public void run(){

        //init remoting server
        NettyServerConfig serverConfig = new NettyServerConfig();
        serverConfig.setListenPort(masterConfig.getListenPort());
        this.nettyRemotingServer = new NettyRemotingServer(serverConfig);
        this.nettyRemotingServer.registerProcessor(CommandType.TASK_EXECUTE_RESPONSE, new TaskResponseProcessor());
        this.nettyRemotingServer.registerProcessor(CommandType.TASK_EXECUTE_ACK, new TaskAckProcessor());
        this.nettyRemotingServer.registerProcessor(CommandType.TASK_KILL_RESPONSE, new TaskKillResponseProcessor());
        this.nettyRemotingServer.start();

        // register
        this.masterRegistry.registry();

        // self tolerant
        this.zkMasterClient.start();

        //
        masterSchedulerService.start();

        // start QuartzExecutors
        // what system should do if exception
        try {
            logger.info("start Quartz server...");
            QuartzExecutors.getInstance().start();
        } catch (Exception e) {
            try {
                QuartzExecutors.getInstance().shutdown();
            } catch (SchedulerException e1) {
                logger.error("QuartzExecutors shutdown failed : " + e1.getMessage(), e1);
            }
            logger.error("start Quartz failed", e);
        }

        /**
         *  register hooks, which are called before the process exits
         */
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                close("shutdownHook");
            }
        }));

    }

    /**
     * gracefully close
     * @param cause close cause
     */
    public void close(String cause) {

        try {
            //execute only once
            if(Stopper.isStopped()){
                return;
            }

            logger.info("master server is stopping ..., cause : {}", cause);

            // set stop signal is true
            Stopper.stop();

            try {
                //thread sleep 3 seconds for thread quietly stop
                Thread.sleep(3000L);
            }catch (Exception e){
                logger.warn("thread sleep exception ", e);
            }
            //
            this.masterSchedulerService.close();
            this.nettyRemotingServer.close();
            this.masterRegistry.unRegistry();
            this.zkMasterClient.close();
            //close quartz
            try{
                QuartzExecutors.getInstance().shutdown();
                logger.info("Quartz service stopped");
            }catch (Exception e){
                logger.warn("Quartz service stopped exception:{}",e.getMessage());
            }
        } catch (Exception e) {
            logger.error("master server stop exception ", e);
            System.exit(-1);
        }
    }
}


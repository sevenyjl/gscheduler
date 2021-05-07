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

package com.gs.cd.gscheduler.server.worker.processor;

import com.gs.cd.gscheduler.server.worker.cache.TaskExecutionContextCacheManager;
import com.gs.cd.gscheduler.server.worker.config.WorkerConfig;
import io.netty.channel.Channel;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.utils.LoggerUtils;
import com.gs.cd.gscheduler.common.utils.OSUtils;
import com.gs.cd.gscheduler.common.utils.Preconditions;
import com.gs.cd.gscheduler.common.utils.StringUtils;
import com.gs.cd.gscheduler.remote.command.Command;
import com.gs.cd.gscheduler.remote.command.CommandType;
import com.gs.cd.gscheduler.remote.command.TaskKillRequestCommand;
import com.gs.cd.gscheduler.remote.command.TaskKillResponseCommand;
import com.gs.cd.gscheduler.remote.processor.NettyRequestProcessor;
import com.gs.cd.gscheduler.remote.utils.FastJsonSerializer;
import com.gs.cd.gscheduler.remote.utils.Host;
import com.gs.cd.gscheduler.remote.utils.Pair;
import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;
import com.gs.cd.gscheduler.server.utils.ProcessUtils;
import com.gs.cd.gscheduler.server.worker.cache.impl.TaskExecutionContextCacheManagerImpl;
import com.gs.cd.gscheduler.service.bean.SpringApplicationContext;
import com.gs.cd.gscheduler.service.log.LogClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

/**
 *  task kill processor
 */
public class TaskKillProcessor implements NettyRequestProcessor {

    private final Logger logger = LoggerFactory.getLogger(TaskKillProcessor.class);

    /**
     *  worker config
     */
    private final WorkerConfig workerConfig;

    /**
     *  task callback service
     */
    private final TaskCallbackService taskCallbackService;

    /**
     * taskExecutionContextCacheManager
     */
    private TaskExecutionContextCacheManager taskExecutionContextCacheManager;


    public TaskKillProcessor(){
        this.taskCallbackService = SpringApplicationContext.getBean(TaskCallbackService.class);
        this.workerConfig = SpringApplicationContext.getBean(WorkerConfig.class);
        this.taskExecutionContextCacheManager = SpringApplicationContext.getBean(TaskExecutionContextCacheManagerImpl.class);
    }

    /**
     * task kill process
     *
     * @param channel channel channel
     * @param command command command
     */
    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_KILL_REQUEST == command.getType(), String.format("invalid command type : %s", command.getType()));
        TaskKillRequestCommand killCommand = FastJsonSerializer.deserialize(command.getBody(), TaskKillRequestCommand.class);
        logger.info("received kill command : {}", killCommand);

        Pair<Boolean, List<String>> result = doKill(killCommand);

        taskCallbackService.addRemoteChannel(killCommand.getTaskInstanceId(),
                new NettyRemoteChannel(channel, command.getOpaque()));

        TaskKillResponseCommand taskKillResponseCommand = buildKillTaskResponseCommand(killCommand,result);
        taskCallbackService.sendResult(taskKillResponseCommand.getTaskInstanceId(), taskKillResponseCommand.convert2Command());
        taskExecutionContextCacheManager.removeByTaskInstanceId(taskKillResponseCommand.getTaskInstanceId());
    }

    /**
     *  do kill
     * @param killCommand
     * @return kill result
     */
    private Pair<Boolean, List<String>> doKill(TaskKillRequestCommand killCommand){
        List<String> appIds = Collections.emptyList();
        try {
            int taskInstanceId = killCommand.getTaskInstanceId();
            TaskExecutionContext taskExecutionContext = taskExecutionContextCacheManager.getByTaskInstanceId(taskInstanceId);

            Integer processId = taskExecutionContext.getProcessId();

            if (processId.equals(0)) {
                taskExecutionContextCacheManager.removeByTaskInstanceId(taskInstanceId);
                logger.info("the task has not been executed and has been cancelled, task id:{}", taskInstanceId);
                return Pair.of(true, appIds);
            }

            String cmd = String.format("sudo kill -9 %s", ProcessUtils.getPidsStr(taskExecutionContext.getProcessId()));

            logger.info("process id:{}, cmd:{}", taskExecutionContext.getProcessId(), cmd);

            OSUtils.exeCmd(cmd);

            // find log and kill yarn job
            appIds = killYarnJob(Host.of(taskExecutionContext.getHost()).getIp(),
                    taskExecutionContext.getLogPath(),
                    taskExecutionContext.getExecutePath(),
                    taskExecutionContext.getTenantCode());

            return Pair.of(true, appIds);
        } catch (Exception e) {
            logger.error("kill task error", e);
        }
        return Pair.of(false, appIds);
    }

    /**
     * build TaskKillResponseCommand
     *
     * @param killCommand  kill command
     * @param result exe result
     * @return build TaskKillResponseCommand
     */
    private TaskKillResponseCommand buildKillTaskResponseCommand(TaskKillRequestCommand killCommand,
                                                                 Pair<Boolean, List<String>> result) {
        TaskKillResponseCommand taskKillResponseCommand = new TaskKillResponseCommand();
        taskKillResponseCommand.setStatus(result.getLeft() ? ExecutionStatus.SUCCESS.getCode() : ExecutionStatus.FAILURE.getCode());
        taskKillResponseCommand.setAppIds(result.getRight());
        TaskExecutionContext taskExecutionContext = taskExecutionContextCacheManager.getByTaskInstanceId(killCommand.getTaskInstanceId());
        if(taskExecutionContext != null){
            taskKillResponseCommand.setTaskInstanceId(taskExecutionContext.getTaskInstanceId());
            taskKillResponseCommand.setHost(taskExecutionContext.getHost());
            taskKillResponseCommand.setProcessId(taskExecutionContext.getProcessId());
        }
        return taskKillResponseCommand;
    }

    /**
     *  kill yarn job
     *
     * @param host host
     * @param logPath logPath
     * @param executePath executePath
     * @param tenantCode tenantCode
     * @return List<String> appIds
     */
    private List<String> killYarnJob(String host, String logPath, String executePath, String tenantCode) {
        LogClientService logClient = null;
        try {
            logClient = new LogClientService();
            logger.info("view log host : {},logPath : {}", host,logPath);
            String log  = logClient.viewLog(host, Constants.RPC_PORT, logPath);

            if (StringUtils.isNotEmpty(log)) {
                List<String> appIds = LoggerUtils.getAppIds(log, logger);
                if (StringUtils.isEmpty(executePath)) {
                    logger.error("task instance execute path is empty");
                    throw new RuntimeException("task instance execute path is empty");
                }
                if (appIds.size() > 0) {
                    ProcessUtils.cancelApplication(appIds, logger, tenantCode, executePath);
                    return appIds;
                }
            }
        } catch (Exception e) {
            logger.error("kill yarn job error",e);
        } finally {
            if(logClient != null){
                logClient.close();
            }
        }
        return Collections.EMPTY_LIST;
    }

}

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

package com.gs.cd.gscheduler.server.master.processor;

import io.netty.channel.Channel;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.utils.Preconditions;
import com.gs.cd.gscheduler.remote.command.Command;
import com.gs.cd.gscheduler.remote.command.CommandType;
import com.gs.cd.gscheduler.remote.command.TaskExecuteResponseCommand;
import com.gs.cd.gscheduler.remote.processor.NettyRequestProcessor;
import com.gs.cd.gscheduler.remote.utils.FastJsonSerializer;
import com.gs.cd.gscheduler.server.master.cache.TaskInstanceCacheManager;
import com.gs.cd.gscheduler.server.master.cache.impl.TaskInstanceCacheManagerImpl;
import com.gs.cd.gscheduler.server.master.processor.queue.TaskResponseEvent;
import com.gs.cd.gscheduler.server.master.processor.queue.TaskResponseService;
import com.gs.cd.gscheduler.service.bean.SpringApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *  task response processor
 */
public class TaskResponseProcessor implements NettyRequestProcessor {

    private final Logger logger = LoggerFactory.getLogger(TaskResponseProcessor.class);

    /**
     * process service
     */
    private final TaskResponseService taskResponseService;

    /**
     * taskInstance cache manager
     */
    private final TaskInstanceCacheManager taskInstanceCacheManager;

    public TaskResponseProcessor(){
        this.taskResponseService = SpringApplicationContext.getBean(TaskResponseService.class);
        this.taskInstanceCacheManager = SpringApplicationContext.getBean(TaskInstanceCacheManagerImpl.class);
    }

    /**
     * task final result response
     * need master process , state persistence
     *
     * @param channel channel
     * @param command command
     */
    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.TASK_EXECUTE_RESPONSE == command.getType(), String.format("invalid command type : %s", command.getType()));

        TaskExecuteResponseCommand responseCommand = FastJsonSerializer.deserialize(command.getBody(), TaskExecuteResponseCommand.class);
        logger.info("received command : {}", responseCommand);

        taskInstanceCacheManager.cacheTaskInstance(responseCommand);

        // TaskResponseEvent
        TaskResponseEvent taskResponseEvent = TaskResponseEvent.newResult(ExecutionStatus.of(responseCommand.getStatus()),
                responseCommand.getEndTime(),
                responseCommand.getProcessId(),
                responseCommand.getAppIds(),
                responseCommand.getTaskInstanceId(),
                channel);
        taskResponseService.addResponse(taskResponseEvent);
    }


}

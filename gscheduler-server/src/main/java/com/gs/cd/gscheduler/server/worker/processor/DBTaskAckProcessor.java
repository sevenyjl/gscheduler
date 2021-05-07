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

import com.gs.cd.gscheduler.remote.command.Command;
import com.gs.cd.gscheduler.remote.command.CommandType;
import com.gs.cd.gscheduler.remote.command.DBTaskAckCommand;
import com.gs.cd.gscheduler.server.worker.cache.ResponceCache;
import io.netty.channel.Channel;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.utils.Preconditions;
import com.gs.cd.gscheduler.remote.processor.NettyRequestProcessor;
import com.gs.cd.gscheduler.remote.utils.FastJsonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  db task ack processor
 */
public class DBTaskAckProcessor implements NettyRequestProcessor {

    private final Logger logger = LoggerFactory.getLogger(DBTaskAckProcessor.class);


    @Override
    public void process(Channel channel, Command command) {
        Preconditions.checkArgument(CommandType.DB_TASK_ACK == command.getType(),
                String.format("invalid command type : %s", command.getType()));

        DBTaskAckCommand taskAckCommand = FastJsonSerializer.deserialize(
                command.getBody(), DBTaskAckCommand.class);

        if (taskAckCommand == null){
            return;
        }

        if (taskAckCommand.getStatus() == ExecutionStatus.SUCCESS.getCode()){
            ResponceCache.get().removeAckCache(taskAckCommand.getTaskInstanceId());
        }
    }


}

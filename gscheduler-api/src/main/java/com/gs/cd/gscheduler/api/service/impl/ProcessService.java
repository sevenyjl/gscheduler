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
package com.gs.cd.gscheduler.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gs.cd.gscheduler.api.service.CommandService;
import com.gs.cd.gscheduler.common.entity.Command;
import com.gs.cd.gscheduler.common.enums.CommandType;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import org.apache.commons.lang.ArrayUtils;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.gs.cd.gscheduler.common.Constants.CMDPARAM_RECOVER_PROCESS_ID_STRING;
import static java.util.stream.Collectors.toSet;

/**
 * process relative dao that some mappers in this.
 */
@Component
public class ProcessService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int[] stateArray = new int[]{ExecutionStatus.SUBMITTED_SUCCESS.ordinal(),
            ExecutionStatus.RUNNING_EXEUTION.ordinal(),
            ExecutionStatus.READY_PAUSE.ordinal(),
            ExecutionStatus.READY_STOP.ordinal()};
    @Autowired
    private CommandService commandService;

    /**
     * check the input command exists in queue list
     *
     * @param command command
     * @return create command result
     */
    public Boolean verifyIsNeedCreateCommand(Command command) {
        Boolean isNeedCreate = true;
        Map<CommandType, Integer> cmdTypeMap = new HashMap<CommandType, Integer>();
        cmdTypeMap.put(CommandType.REPEAT_RUNNING, 1);
        cmdTypeMap.put(CommandType.RECOVER_SUSPENDED_PROCESS, 1);
        cmdTypeMap.put(CommandType.START_FAILURE_TASK_PROCESS, 1);
        CommandType commandType = command.getCommandType();

        if (cmdTypeMap.containsKey(commandType)) {
            JSONObject cmdParamObj = (JSONObject) JSON.parse(command.getCommandParam());
            JSONObject tempObj;
            int processInstanceId = cmdParamObj.getInteger(CMDPARAM_RECOVER_PROCESS_ID_STRING);
            List<Command> commands = commandService.list();
            // todo 查询优化
            for (Command tmpCommand : commands) {
                if (cmdTypeMap.containsKey(tmpCommand.getCommandType())) {
                    tempObj = (JSONObject) JSON.parse(tmpCommand.getCommandParam());
                    if (tempObj != null && processInstanceId == tempObj.getInteger(CMDPARAM_RECOVER_PROCESS_ID_STRING)) {
                        isNeedCreate = false;
                        break;
                    }
                }
            }
        }
        return isNeedCreate;
    }

}

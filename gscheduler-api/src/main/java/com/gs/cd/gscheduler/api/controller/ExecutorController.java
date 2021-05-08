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
package com.gs.cd.gscheduler.api.controller;


import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.common.enums.*;
import com.gs.cd.gscheduler.api.enums.ExecuteType;
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.service.ExecutorService;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.Constants;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.text.ParseException;
import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;


/**
 * execute process controller
 */
@Api(tags = "PROCESS_INSTANCE_EXECUTOR_TAG", position = 1)
@RestController
@RequestMapping("projects/{projectName}/executors")
public class ExecutorController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ExecutorController.class);

    @Autowired
    private ExecutorService execService;

    /**
     * execute process instance
     *
     * @param projectName             project name
     * @param processDefinitionId     process definition id
     * @param scheduleTime            schedule time
     * @param failureStrategy         failure strategy
     * @param startNodeList           start nodes list
     * @param taskDependType          task depend type
     * @param execType                execute type
     * @param warningType             warning type
     * @param warningGroupId          warning group id
     * @param receivers               receivers
     * @param receiversCc             receivers cc
     * @param runMode                 run mode
     * @param processInstancePriority process instance priority
     * @param workerGroup             worker group
     * @param timeout                 timeout
     * @return start process result code
     */


    @PostMapping(value = "start-process-instance")


    public Result startProcessInstance(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                       @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                       @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                       @RequestParam(value = "processDefinitionId") int processDefinitionId,
                                       @RequestParam(value = "scheduleTime", required = false) String scheduleTime,
                                       @RequestParam(value = "failureStrategy", required = true) FailureStrategy failureStrategy,
                                       @RequestParam(value = "startNodeList", required = false) String startNodeList,
                                       @RequestParam(value = "taskDependType", required = false) TaskDependType taskDependType,
                                       @RequestParam(value = "execType", required = false) CommandType execType,
                                       @RequestParam(value = "warningType", required = true) WarningType warningType,
                                       @RequestParam(value = "warningGroupId", required = false) int warningGroupId,
                                       @RequestParam(value = "receivers", required = false) String receivers,
                                       @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                       @RequestParam(value = "runMode", required = false) RunMode runMode,
                                       @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority,
                                       @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                       @RequestParam(value = "timeout", required = false) Integer timeout) throws ParseException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, start process instance, project name: {}, process definition id: {}, schedule time: {}, "
                        + "failure policy: {}, node name: {}, node dep: {}, notify type: {}, "
                        + "notify group id: {},receivers:{},receiversCc:{}, run mode: {},process instance priority:{}, workerGroup: {}, timeout: {}",
                loginUser.getUserName(), projectName, processDefinitionId, scheduleTime,
                failureStrategy, startNodeList, taskDependType, warningType, workerGroup, receivers, receiversCc, runMode, processInstancePriority,
                workerGroup, timeout);

        if (timeout == null) {
            timeout = Constants.MAX_TASK_TIMEOUT;
        }

        Map<String, Object> result = execService.execProcessInstance(loginUser, projectName, processDefinitionId, scheduleTime, execType, failureStrategy,
                startNodeList, taskDependType, warningType,
                warningGroupId, receivers, receiversCc, runMode, processInstancePriority, workerGroup, timeout);
        return returnDataList(result);
    }


    /**
     * do action to process instance：pause, stop, repeat, recover from pause, recover from stop
     *
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @param executeType       execute type
     * @return execute result code
     */


    @PostMapping(value = "/execute")


    public Result execute(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                          @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                          @RequestParam("processInstanceId") Integer processInstanceId,
                          @RequestParam("executeType") ExecuteType executeType
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("execute command, login user: {}, project:{}, process instance id:{}, execute type:{}",
                loginUser.getUserName(), projectName, processInstanceId, executeType);
        Map<String, Object> result = execService.execute(loginUser, projectName, processInstanceId, executeType);
        return returnDataList(result);
    }

    /**
     * check process definition and all of the son process definitions is on line.
     *
     * @param processDefinitionId process definition id
     * @return check result code
     */


    @PostMapping(value = "/start-check")


    public Result startCheckProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @RequestParam(value = "processDefinitionId") int processDefinitionId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, check process definition {}", loginUser.getUserName(), processDefinitionId);
        Map<String, Object> result = execService.startCheckByProcessDefinedId(processDefinitionId);
        return returnDataList(result);
    }

    /**
     * query recipients and copyers by process definition ID
     *
     * @param processDefinitionId process definition id
     * @param processInstanceId   process instance id
     * @return receivers cc list
     */
    @ApiIgnore


    @GetMapping(value = "/get-receiver-cc")


    public Result getReceiverCc(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                @RequestParam(value = "processDefinitionId", required = false) Integer processDefinitionId,
                                @RequestParam(value = "processInstanceId", required = false) Integer processInstanceId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, get process definition receiver and cc", loginUser.getUserName());
        Map<String, Object> result = execService.getReceiverCc(processDefinitionId, processInstanceId);
        return returnDataList(result);
    }


}

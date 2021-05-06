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


import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.enums.ExecuteType;
import com.gs.cd.gscheduler.api.service.impl.ExecutorService;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Map;


/**
 * 执行
 */

@RestController
@RequestMapping("/gscheduler/projects/{projectName}/executors")
@Slf4j
public class ExecutorController {

    @Autowired
    private ExecutorService executorService;

    @PostMapping(value = "start-process-instance")
    public ApiResult startProcessInstance(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                          @PathVariable String projectName,
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
                                          @RequestParam(value = "timeout", required = false) Integer timeout) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, start process instance, project name: {}, process definition id: {}, schedule time: {}, "
                        + "failure policy: {}, node name: {}, node dep: {}, notify type: {}, "
                        + "notify group id: {},receivers:{},receiversCc:{}, run mode: {},process instance priority:{}, workerGroup: {}, timeout: {}",
                loginUser.getUserName(), projectName, processDefinitionId, scheduleTime,
                failureStrategy, startNodeList, taskDependType, warningType, workerGroup, receivers, receiversCc, runMode, processInstancePriority,
                workerGroup, timeout);

        if (timeout == null) {
            timeout = Constants.MAX_TASK_TIMEOUT;
        }
        executorService.execProcessInstance(loginUser, projectName, processDefinitionId, scheduleTime, execType, failureStrategy,
                startNodeList, taskDependType, warningType,
                warningGroupId, receivers, receiversCc, runMode, processInstancePriority, workerGroup, timeout);
        return ApiResult.success();
    }


    /**
     * do action to process instance：pause, stop, repeat, recover from pause, recover from stop
     *
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @param executeType       execute type
     * @return execute ApiResult code
     */


    @PostMapping(value = "/execute")
    public ApiResult execute(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                             @PathVariable String projectName,
                             @RequestParam("processInstanceId") Integer processInstanceId,
                             @RequestParam("executeType") ExecuteType executeType
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("execute command, login user: {}, project:{}, process instance id:{}, execute type:{}",
                loginUser.getUserName(), projectName, processInstanceId, executeType);
        executorService.execute(loginUser, projectName, processInstanceId, executeType);
        return ApiResult.success();
    }

    /**
     * check process definition and all of the son process definitions is on line.
     *
     * @param processDefinitionId process definition id
     * @return check ApiResult code
     */


    @PostMapping(value = "/start-check")
    public ApiResult startCheckProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                 @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                 @RequestParam(value = "processDefinitionId") int processDefinitionId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, check process definition {}", loginUser.getUserName(), processDefinitionId);
        executorService.startCheckByProcessDefinedId(processDefinitionId);
        return ApiResult.success();
    }

}

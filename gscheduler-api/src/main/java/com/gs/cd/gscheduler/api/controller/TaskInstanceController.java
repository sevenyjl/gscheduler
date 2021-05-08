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
import com.gs.cd.gscheduler.api.service.TaskInstanceService;
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.QUERY_TASK_LIST_PAGING_ERROR;

/**
 * task instance controller
 */
@Api(tags = "TASK_INSTANCE_TAG", position = 11)
@RestController
@RequestMapping("/projects/{projectName}/task-instance")
public class TaskInstanceController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(TaskInstanceController.class);

    @Autowired
    TaskInstanceService taskInstanceService;


    /**
     * query task list paging
     *
     * @param loginUser         login user
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @param searchVal         search value
     * @param taskName          task name
     * @param stateType         state type
     * @param host              host
     * @param startTime         start time
     * @param endTime           end time
     * @param pageNo            page number
     * @param pageSize          page size
     * @return task list page
     */


    @GetMapping("/list-paging")


    public Result queryTaskListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                      @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                      @RequestParam(value = "processInstanceId", required = false, defaultValue = "0") Integer processInstanceId,
                                      @RequestParam(value = "searchVal", required = false) String searchVal,
                                      @RequestParam(value = "taskName", required = false) String taskName,
                                      @RequestParam(value = "executorName", required = false) String executorName,
                                      @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                      @RequestParam(value = "host", required = false) String host,
                                      @RequestParam(value = "startDate", required = false) String startTime,
                                      @RequestParam(value = "endDate", required = false) String endTime,
                                      @RequestParam("pageNo") Integer pageNo,
                                      @RequestParam("pageSize") Integer pageSize) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query task instance list, project name:{},process instance:{}, search value:{},task name:{}, executor name: {},state type:{}, host:{}, start:{}, end:{}",
                projectName, processInstanceId, searchVal, taskName, executorName, stateType, host, startTime, endTime);
        searchVal = ParameterUtils.handleEscapes(searchVal);
        Map<String, Object> result = taskInstanceService.queryTaskListPaging(
                loginUser, projectName, processInstanceId, taskName, executorName, startTime, endTime, searchVal, stateType, host, pageNo, pageSize);
        return returnDataListPaging(result);
    }

}

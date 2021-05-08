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
package com.gs.cd.gscheduler.server.controller;


import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;

import com.gs.cd.cloud.common.ApiResult;

import com.gs.cd.gscheduler.api.TaskInstanceApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.apache.dolphinscheduler.common.enums.ExecutionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/projects/{projectName}/task-instance")
public class TaskInstanceController {
    @Autowired
    TaskInstanceApi taskInstanceApi;


    @GetMapping("/list-paging")
    public ApiResult queryTaskListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                         @PathVariable String projectName,
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
        return taskInstanceApi.queryTaskListPaging(TenantCodeService.getSessionId(tenantCode), projectName, processInstanceId,
                searchVal, taskName, executorName, stateType, host, startTime, endTime, pageNo, pageSize).apiResult();
    }

}

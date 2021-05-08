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
import com.gs.cd.gscheduler.api.TaskRecordApi;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * data quality controller
 */

@RestController
@RequestMapping("/projects/task-record")
public class TaskRecordController {
    @Autowired
    private TaskRecordApi taskRecordApi;


    @GetMapping("/list-paging")
    public ApiResult queryTaskRecordListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                               @RequestParam(value = "taskName", required = false) String taskName,
                                               @RequestParam(value = "state", required = false) String state,
                                               @RequestParam(value = "sourceTable", required = false) String sourceTable,
                                               @RequestParam(value = "destTable", required = false) String destTable,
                                               @RequestParam(value = "taskDate", required = false) String taskDate,
                                               @RequestParam(value = "startDate", required = false) String startTime,
                                               @RequestParam(value = "endDate", required = false) String endTime,
                                               @RequestParam("pageNo") Integer pageNo,
                                               @RequestParam("pageSize") Integer pageSize
    ) {
        return taskRecordApi.queryTaskRecordListPaging(TenantCodeService.getSessionId(tenantCode), taskName, state,
                sourceTable, destTable, taskDate, startTime, endTime, pageNo, pageSize).apiResult();
    }

    @GetMapping("/history-list-paging")
    public ApiResult queryHistoryTaskRecordListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                      @RequestParam(value = "taskName", required = false) String taskName,
                                                      @RequestParam(value = "state", required = false) String state,
                                                      @RequestParam(value = "sourceTable", required = false) String sourceTable,
                                                      @RequestParam(value = "destTable", required = false) String destTable,
                                                      @RequestParam(value = "taskDate", required = false) String taskDate,
                                                      @RequestParam(value = "startDate", required = false) String startTime,
                                                      @RequestParam(value = "endDate", required = false) String endTime,
                                                      @RequestParam("pageNo") Integer pageNo,
                                                      @RequestParam("pageSize") Integer pageSize
    ) {

        return taskRecordApi.queryHistoryTaskRecordListPaging(TenantCodeService.getSessionId(tenantCode), taskName, state,
                sourceTable, destTable, taskDate, startTime, endTime, pageNo, pageSize).apiResult();
    }

}

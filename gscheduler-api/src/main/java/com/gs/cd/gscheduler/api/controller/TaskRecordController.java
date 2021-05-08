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
import com.gs.cd.gscheduler.api.service.TaskRecordService;
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;

/**
 * data quality controller
 */

@RestController
@RequestMapping("/projects/task-record")
public class TaskRecordController extends BaseController {


    private static final Logger logger = LoggerFactory.getLogger(TaskRecordController.class);


    @Autowired
    TaskRecordService taskRecordService;

    /**
     * query task record list page
     *
     * @param taskName    task name
     * @param state       state
     * @param sourceTable source table
     * @param destTable   destination table
     * @param taskDate    task date
     * @param startTime   start time
     * @param endTime     end time
     * @param pageNo      page numbere
     * @param pageSize    page size
     * @return task record list
     */
    @GetMapping("/list-paging")


    public Result queryTaskRecordListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
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

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query task record list, task name:{}, state :{}, taskDate: {}, start:{}, end:{}",
                taskName, state, taskDate, startTime, endTime);
        Map<String, Object> result = taskRecordService.queryTaskRecordListPaging(false, taskName, startTime, taskDate, sourceTable, destTable, endTime, state, pageNo, pageSize);
        return returnDataListPaging(result);
    }

    /**
     * query history task record list paging
     *
     * @param loginUser   login user
     * @param taskName    task name
     * @param state       state
     * @param sourceTable source table
     * @param destTable   destination table
     * @param taskDate    task date
     * @param startTime   start time
     * @param endTime     end time
     * @param pageNo      page number
     * @param pageSize    page size
     * @return history task record list
     */
    @GetMapping("/history-list-paging")


    public Result queryHistoryTaskRecordListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                   @RequestHeader(HttpHeadersParam.TOKEN) String token,
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
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query hisotry task record list, task name:{}, state :{}, taskDate: {}, start:{}, end:{}",
                taskName, state, taskDate, startTime, endTime);
        Map<String, Object> result = taskRecordService.queryTaskRecordListPaging(true, taskName, startTime, taskDate, sourceTable, destTable, endTime, state, pageNo, pageSize);
        return returnDataListPaging(result);
    }

}

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

import com.gs.cd.gscheduler.api.SchedulerApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.apache.dolphinscheduler.common.enums.FailureStrategy;
import org.apache.dolphinscheduler.common.enums.Priority;
import org.apache.dolphinscheduler.common.enums.WarningType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.gs.cd.gscheduler.api.SchedulerApi.*;


/**
 * schedule controller
 */

@RestController
@RequestMapping("/projects/{projectName}/schedule")
public class SchedulerController {

    @Autowired
    private SchedulerApi schedulerApi;

    @PostMapping("/create")
    public ApiResult createSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @PathVariable String projectName,
                                    @RequestParam(value = "processDefinitionId") Integer processDefinitionId,
                                    @RequestParam(value = "schedule") String schedule,
                                    @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                    @RequestParam(value = "warningGroupId", required = false, defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                                    @RequestParam(value = "failureStrategy", required = false, defaultValue = DEFAULT_FAILURE_POLICY) FailureStrategy failureStrategy,
                                    @RequestParam(value = "receivers", required = false) String receivers,
                                    @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                    @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                    @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority) throws IOException {
        return schedulerApi.createSchedule(TenantCodeService.getSessionId(tenantCode), projectName, processDefinitionId, schedule, warningType, warningGroupId,
                failureStrategy, receivers, receiversCc, workerGroup, processInstancePriority).apiResult();
    }

    @PostMapping("/update")
    public ApiResult updateSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @PathVariable String projectName,
                                    @RequestParam(value = "id") Integer id,
                                    @RequestParam(value = "schedule") String schedule,
                                    @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                    @RequestParam(value = "warningGroupId", required = false) int warningGroupId,
                                    @RequestParam(value = "failureStrategy", required = false, defaultValue = "END") FailureStrategy failureStrategy,
                                    @RequestParam(value = "receivers", required = false) String receivers,
                                    @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                    @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                    @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority) throws IOException {
        return schedulerApi.updateSchedule(TenantCodeService.getSessionId(tenantCode), projectName, id, schedule, warningType,
                warningGroupId, failureStrategy, receivers, receiversCc, workerGroup, processInstancePriority).apiResult();
    }


    @PostMapping("/online")
    public ApiResult online(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @PathVariable("projectName") String projectName,
                            @RequestParam("id") Integer id) {
        return schedulerApi.online(TenantCodeService.getSessionId(tenantCode), projectName, id).apiResult();
    }

    @PostMapping("/offline")
    public ApiResult offline(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                             @PathVariable("projectName") String projectName,
                             @RequestParam("id") Integer id) {
        return schedulerApi.offline(TenantCodeService.getSessionId(tenantCode), projectName, id).apiResult();
    }

    @GetMapping("/list-paging")
    public ApiResult queryScheduleListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @PathVariable String projectName,
                                             @RequestParam Integer processDefinitionId,
                                             @RequestParam(value = "searchVal", required = false) String searchVal,
                                             @RequestParam("pageNo") Integer pageNo,
                                             @RequestParam("pageSize") Integer pageSize) {
        return schedulerApi.queryScheduleListPaging(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId, searchVal, pageNo, pageSize).apiResult();
    }

    @GetMapping(value = "/delete")
    public ApiResult deleteScheduleById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                        @PathVariable String projectName,
                                        @RequestParam("scheduleId") Integer scheduleId
    ) {
        return schedulerApi.deleteScheduleById(TenantCodeService.getSessionId(tenantCode),
                projectName, scheduleId).apiResult();
    }

    @PostMapping("/list")
    public ApiResult queryScheduleList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                       @PathVariable String projectName) {
        return schedulerApi.queryScheduleList(TenantCodeService.getSessionId(tenantCode), projectName).apiResult();
    }


    @PostMapping("/preview")
    public ApiResult previewSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                     @PathVariable String projectName,
                                     @RequestParam(value = "schedule") String schedule
    ) {
        return schedulerApi.previewSchedule(TenantCodeService.getSessionId(tenantCode), projectName, schedule).apiResult();
    }
}

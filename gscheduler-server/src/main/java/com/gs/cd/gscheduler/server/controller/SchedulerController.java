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


import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.common.HttpHeadersParam;

import com.gs.cd.cloud.common.ApiResult;

import com.gs.cd.gscheduler.api.SchedulerApi;
import com.gs.cd.gscheduler.entity.Schedule;
import com.gs.cd.gscheduler.server.Constant;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import com.gs.cd.gscheduler.server.service.impl.PurviewCheckService;
import com.gs.cd.gscheduler.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.common.enums.FailureStrategy;
import org.apache.dolphinscheduler.common.enums.Priority;
import org.apache.dolphinscheduler.common.enums.ReleaseState;
import org.apache.dolphinscheduler.common.enums.WarningType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.gs.cd.gscheduler.api.SchedulerApi.*;


/**
 * schedule controller
 */

@RestController
@Slf4j
@RequestMapping("/projects/{projectName}/schedule")
public class SchedulerController {

    @Autowired
    private SchedulerApi schedulerApi;
    @Autowired
    PurviewCheckService purviewCheckService;

    /**
     * 添加Or修改
     * 更具id判断
     *
     * @param tenantCode
     * @param projectName
     * @param id
     * @param processDefinitionId
     * @param releaseState            上线状态
     * @param schedule
     * @param warningType
     * @param warningGroupId
     * @param failureStrategy
     * @param receivers
     * @param receiversCc
     * @param workerGroup
     * @param processInstancePriority
     * @return
     * @throws IOException
     */
    @PostMapping("coru")
    public ApiResult coru(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                          @PathVariable String projectName,
                          @RequestParam(value = "id", required = false) Integer id,
                          @RequestParam(value = "processDefinitionId") Integer processDefinitionId,
                          @RequestParam ReleaseState releaseState,
                          @RequestParam(value = "schedule") String schedule,
                          @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                          @RequestParam(value = "warningGroupId", required = false, defaultValue = "0") int warningGroupId,
                          @RequestParam(value = "failureStrategy", required = false, defaultValue = "END") FailureStrategy failureStrategy,
                          @RequestParam(value = "receivers", required = false) String receivers,
                          @RequestParam(value = "receiversCc", required = false) String receiversCc,
                          @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                          @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority) throws IOException {
        purviewCheckService.check("定时运行",Constant.ProcessDefinitionPerms.timed_run, token, tenantCode);
        if (id == null) {
            Result create = schedulerApi.createSchedule(TenantCodeService.getSessionId(tenantCode), projectName, processDefinitionId, schedule, warningType, warningGroupId,
                    failureStrategy, receivers, receiversCc, workerGroup, processInstancePriority);
            if (!create.isSuccess()) {
                return create.apiResult();
            }
            int createId = Integer.parseInt(create.getData().toString());
            if (releaseState == ReleaseState.ONLINE) {
                return schedulerApi.online(TenantCodeService.getSessionId(tenantCode), projectName, createId).apiResult();
            }
            return createSchedule(tenantCode, projectName, processDefinitionId, schedule, warningType, warningGroupId, failureStrategy, receivers, receiversCc, workerGroup, processInstancePriority);
        } else {
            Result offline = schedulerApi.offline(TenantCodeService.getSessionId(tenantCode), projectName, id);
            Result updateSchedule = schedulerApi.updateSchedule(TenantCodeService.getSessionId(tenantCode), projectName, id, schedule, warningType,
                    warningGroupId, failureStrategy, receivers, receiversCc, workerGroup, processInstancePriority);
            if (!updateSchedule.isSuccess()) {
                return updateSchedule.apiResult();
            }
            if (releaseState == ReleaseState.ONLINE) {
                return schedulerApi.online(TenantCodeService.getSessionId(tenantCode), projectName, id).apiResult();
            } else {
                return updateSchedule.apiResult();
            }
        }
    }

    /**
     * 获取当前工作流的定时任务
     *
     * @param tenantCode
     * @param projectName
     * @param processDefinitionId
     * @return
     */
    @GetMapping("getOne")
    public ApiResult getOneSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @PathVariable String projectName,
                                    @RequestParam Integer processDefinitionId) {
        Result result = schedulerApi.queryScheduleListPaging(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId, "", 1, 1);
        Schedule schedule = null;
        if (result.isSuccess()) {
            JSONArray totalList = JSONUtil.parseObj(result.getData()).getJSONArray("totalList");
            if (totalList.size() > 0) {
                JSONObject jsonObject = totalList.getJSONObject(0);
                schedule = jsonObject.toBean(Schedule.class);
                return ApiResult.success(schedule);
            } else {
                return ApiResult.success(schedule);
            }
        } else {
            return result.apiResult();
        }
    }

    /**
     * 删除工作流的定时任务
     *
     * @param tenantCode
     * @param projectName
     * @param processDefinitionId
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteScheduleById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                        @PathVariable String projectName,
                                        @RequestParam Integer processDefinitionId
    ) {
        Result result = schedulerApi.queryScheduleListPaging(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId, "", 1, 9999);
        if (result.isSuccess()) {
            JSONArray totalList = JSONUtil.parseObj(result.getData()).getJSONArray("totalList");
            List<Result> results = new ArrayList<>();
            totalList.forEach(s -> {
                JSONObject j = (JSONObject) s;
                Result r = schedulerApi.deleteScheduleById(TenantCodeService.getSessionId(tenantCode),
                        projectName, j.getInt("id"));
                results.add(r);
            });
            log.debug("删除定时任务：{}", results);
            return ApiResult.success();
        } else {
            return result.apiResult();
        }
    }


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
        Result create = schedulerApi.createSchedule(TenantCodeService.getSessionId(tenantCode), projectName, processDefinitionId, schedule, warningType, warningGroupId,
                failureStrategy, receivers, receiversCc, workerGroup, processInstancePriority);
        int createId = Integer.parseInt(create.getData().toString());
        //上线定时管理
        return schedulerApi.online(TenantCodeService.getSessionId(tenantCode), projectName, createId).apiResult();
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
        //下线定时管理
        Result offline = schedulerApi.offline(TenantCodeService.getSessionId(tenantCode), projectName, id);
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

//    @GetMapping(value = "/delete")
//    public ApiResult deleteScheduleById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                                        @PathVariable String projectName,
//                                        @RequestParam("scheduleId") Integer scheduleId
//    ) {
//        return schedulerApi.deleteScheduleById(TenantCodeService.getSessionId(tenantCode),
//                projectName, scheduleId).apiResult();
//    }

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

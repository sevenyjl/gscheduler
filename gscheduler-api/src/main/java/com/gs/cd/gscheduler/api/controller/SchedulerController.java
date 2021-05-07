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
import com.gs.cd.gscheduler.api.service.SchedulerService;
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.enums.FailureStrategy;
import com.gs.cd.gscheduler.common.enums.Priority;
import com.gs.cd.gscheduler.common.enums.ReleaseState;
import com.gs.cd.gscheduler.common.enums.WarningType;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;

/**
 * schedule controller
 */
@Api(tags = "SCHEDULER_TAG", position = 13)
@RestController
@RequestMapping("/projects/{projectName}/schedule")
public class SchedulerController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);
    public static final String DEFAULT_WARNING_TYPE = "NONE";
    public static final String DEFAULT_NOTIFY_GROUP_ID = "1";
    public static final String DEFAULT_FAILURE_POLICY = "CONTINUE";


    @Autowired
    private SchedulerService schedulerService;


    /**
     * create schedule
     *
     * @param projectName             project name
     * @param processDefinitionId     process definition id
     * @param schedule                scheduler
     * @param warningType             warning type
     * @param warningGroupId          warning group id
     * @param failureStrategy         failure strategy
     * @param processInstancePriority process instance priority
     * @param receivers               receivers
     * @param receiversCc             receivers cc
     * @param workerGroup             worker group
     * @return create result code
     */
    @ApiOperation(value = "createSchedule", notes = "CREATE_SCHEDULE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "schedule", value = "SCHEDULE", dataType = "String", example = "{'startTime':'2019-06-10 00:00:00','endTime':'2019-06-13 00:00:00','crontab':'0 0 3/6 * * ? *'}"),
            @ApiImplicitParam(name = "warningType", value = "WARNING_TYPE", type = "WarningType"),
            @ApiImplicitParam(name = "warningGroupId", value = "WARNING_GROUP_ID", dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "failureStrategy", value = "FAILURE_STRATEGY", type = "FailureStrategy"),
            @ApiImplicitParam(name = "receivers", value = "RECEIVERS", type = "String"),
            @ApiImplicitParam(name = "receiversCc", value = "RECEIVERS_CC", type = "String"),
            @ApiImplicitParam(name = "workerGroupId", value = "WORKER_GROUP_ID", dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "processInstancePriority", value = "PROCESS_INSTANCE_PRIORITY", type = "Priority"),
    })
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(CREATE_SCHEDULE_ERROR)
    public Result createSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                 @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                 @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                 @RequestParam(value = "processDefinitionId") Integer processDefinitionId,
                                 @RequestParam(value = "schedule") String schedule,
                                 @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                 @RequestParam(value = "warningGroupId", required = false, defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                                 @RequestParam(value = "failureStrategy", required = false, defaultValue = DEFAULT_FAILURE_POLICY) FailureStrategy failureStrategy,
                                 @RequestParam(value = "receivers", required = false) String receivers,
                                 @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                 @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                 @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority) throws IOException {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, project name: {}, process name: {}, create schedule: {}, warning type: {}, warning group id: {}," +
                        "failure policy: {},receivers : {},receiversCc : {},processInstancePriority : {}, workGroupId:{}",
                loginUser.getUserName(), projectName, processDefinitionId, schedule, warningType, warningGroupId,
                failureStrategy, receivers, receiversCc, processInstancePriority, workerGroup);
        Map<String, Object> result = schedulerService.insertSchedule(loginUser, projectName, processDefinitionId, schedule,
                warningType, warningGroupId, failureStrategy, receivers, receiversCc, processInstancePriority, workerGroup);

        return returnDataList(result);
    }

    /**
     * updateProcessInstance schedule
     *
     * @param projectName             project name
     * @param id                      scheduler id
     * @param schedule                scheduler
     * @param warningType             warning type
     * @param warningGroupId          warning group id
     * @param failureStrategy         failure strategy
     * @param receivers               receivers
     * @param workerGroup             worker group
     * @param processInstancePriority process instance priority
     * @param receiversCc             receivers cc
     * @return update result code
     */
    @ApiOperation(value = "updateSchedule", notes = "UPDATE_SCHEDULE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "schedule", value = "SCHEDULE", dataType = "String", example = "{'startTime':'2019-06-10 00:00:00','endTime':'2019-06-13 00:00:00','crontab':'0 0 3/6 * * ? *'}"),
            @ApiImplicitParam(name = "warningType", value = "WARNING_TYPE", type = "WarningType"),
            @ApiImplicitParam(name = "warningGroupId", value = "WARNING_GROUP_ID", dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "failureStrategy", value = "FAILURE_STRATEGY", type = "FailureStrategy"),
            @ApiImplicitParam(name = "receivers", value = "RECEIVERS", type = "String"),
            @ApiImplicitParam(name = "receiversCc", value = "RECEIVERS_CC", type = "String"),
            @ApiImplicitParam(name = "workerGroupId", value = "WORKER_GROUP_ID", dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "processInstancePriority", value = "PROCESS_INSTANCE_PRIORITY", type = "Priority"),
    })
    @PostMapping("/update")
    @ApiException(UPDATE_SCHEDULE_ERROR)
    public Result updateSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                 @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                 @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                 @RequestParam(value = "id") Integer id,
                                 @RequestParam(value = "schedule") String schedule,
                                 @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                 @RequestParam(value = "warningGroupId", required = false) int warningGroupId,
                                 @RequestParam(value = "failureStrategy", required = false, defaultValue = "END") FailureStrategy failureStrategy,
                                 @RequestParam(value = "receivers", required = false) String receivers,
                                 @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                 @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                 @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority) throws IOException {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, project name: {},id: {}, updateProcessInstance schedule: {}, notify type: {}, notify mails: {}, " +
                        "failure policy: {},receivers : {},receiversCc : {},processInstancePriority : {},workerGroupId:{}",
                loginUser.getUserName(), projectName, id, schedule, warningType, warningGroupId, failureStrategy,
                receivers, receiversCc, processInstancePriority, workerGroup);

        Map<String, Object> result = schedulerService.updateSchedule(loginUser, projectName, id, schedule,
                warningType, warningGroupId, failureStrategy, receivers, receiversCc, null, processInstancePriority, workerGroup);
        return returnDataList(result);
    }

    /**
     * publish schedule setScheduleState
     *
     * @param projectName project name
     * @param id          scheduler id
     * @return publish result code
     */
    @ApiOperation(value = "online", notes = "ONLINE_SCHEDULE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100")
    })
    @PostMapping("/online")
    @ApiException(PUBLISH_SCHEDULE_ONLINE_ERROR)
    public Result online(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                         @RequestHeader(HttpHeadersParam.TOKEN) String token,
                         @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable("projectName") String projectName,
                         @RequestParam("id") Integer id) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, schedule setScheduleState, project name: {}, id: {}",
                loginUser.getUserName(), projectName, id);
        Map<String, Object> result = schedulerService.setScheduleState(loginUser, projectName, id, ReleaseState.ONLINE);
        return returnDataList(result);
    }

    /**
     * offline schedule
     *
     * @param projectName project name
     * @param id          schedule id
     * @return operation result code
     */
    @ApiOperation(value = "offline", notes = "OFFLINE_SCHEDULE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100")
    })
    @PostMapping("/offline")
    @ApiException(OFFLINE_SCHEDULE_ERROR)
    public Result offline(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                          @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable("projectName") String projectName,
                          @RequestParam("id") Integer id) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, schedule offline, project name: {}, process definition id: {}",
                loginUser.getUserName(), projectName, id);

        Map<String, Object> result = schedulerService.setScheduleState(loginUser, projectName, id, ReleaseState.OFFLINE);
        return returnDataList(result);
    }

    /**
     * query schedule list paging
     *
     * @param projectName         project name
     * @param processDefinitionId process definition id
     * @param pageNo              page number
     * @param pageSize            page size
     * @param searchVal           search value
     * @return schedule list page
     */
    @ApiOperation(value = "queryScheduleListPaging", notes = "QUERY_SCHEDULE_LIST_PAGING_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "searchVal", value = "SEARCH_VAL", type = "String"),
            @ApiImplicitParam(name = "pageNo", value = "PAGE_NO", dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "pageSize", value = "PAGE_SIZE", dataType = "Int", example = "100")

    })
    @GetMapping("/list-paging")
    @ApiException(QUERY_SCHEDULE_LIST_PAGING_ERROR)
    public Result queryScheduleListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                          @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                          @RequestParam Integer processDefinitionId,
                                          @RequestParam(value = "searchVal", required = false) String searchVal,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam("pageSize") Integer pageSize) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query schedule, project name: {}, process definition id: {}",
                loginUser.getUserName(), projectName, processDefinitionId);
        searchVal = ParameterUtils.handleEscapes(searchVal);
        Map<String, Object> result = schedulerService.querySchedule(loginUser, projectName, processDefinitionId, searchVal, pageNo, pageSize);
        return returnDataListPaging(result);
    }

    /**
     * delete schedule by id
     *
     * @param projectName project name
     * @param scheduleId  scheule id
     * @return delete result code
     */
    @ApiOperation(value = "deleteScheduleById", notes = "OFFLINE_SCHEDULE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "scheduleId", value = "SCHEDULE_ID", required = true, dataType = "Int", example = "100")
    })
    @GetMapping(value = "/delete")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(DELETE_SCHEDULE_CRON_BY_ID_ERROR)
    public Result deleteScheduleById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                     @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                     @PathVariable String projectName,
                                     @RequestParam("scheduleId") Integer scheduleId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("delete schedule by id, login user:{}, project name:{}, schedule id:{}",
                loginUser.getUserName(), projectName, scheduleId);
        Map<String, Object> result = schedulerService.deleteScheduleById(loginUser, projectName, scheduleId);
        return returnDataList(result);
    }

    /**
     * query schedule list
     *
     * @param projectName project name
     * @return schedule list
     */
    @ApiOperation(value = "queryScheduleList", notes = "QUERY_SCHEDULE_LIST_NOTES")
    @PostMapping("/list")
    @ApiException(QUERY_SCHEDULE_LIST_ERROR)
    public Result queryScheduleList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                    @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query schedule list, project name: {}",
                loginUser.getUserName(), projectName);
        Map<String, Object> result = schedulerService.queryScheduleList(loginUser, projectName);
        return returnDataList(result);
    }

    /**
     * preview schedule
     *
     * @param projectName project name
     * @param schedule    schedule expression
     * @return the next five fire time
     */
    @ApiOperation(value = "previewSchedule", notes = "PREVIEW_SCHEDULE_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "schedule", value = "SCHEDULE", dataType = "String", example = "{'startTime':'2019-06-10 00:00:00','endTime':'2019-06-13 00:00:00','crontab':'0 0 3/6 * * ? *'}"),
    })
    @PostMapping("/preview")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiException(PREVIEW_SCHEDULE_ERROR)
    public Result previewSchedule(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                  @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                  @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                  @RequestParam(value = "schedule") String schedule
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, project name: {}, preview schedule: {}",
                loginUser.getUserName(), projectName, schedule);
        Map<String, Object> result = schedulerService.previewSchedule(loginUser, projectName, schedule);
        return returnDataList(result);
    }
}

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
import com.gs.cd.gscheduler.api.enums.Status;
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.service.ProcessInstanceService;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.enums.Flag;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import com.gs.cd.gscheduler.common.utils.StringUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;

/**
 * process instance controller
 */
@Api(tags = "PROCESS_INSTANCE_TAG", position = 10)
@RestController
@RequestMapping("projects/{projectName}/instance")
public class ProcessInstanceController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceController.class);


    @Autowired
    ProcessInstanceService processInstanceService;

    /**
     * query process instance list paging
     *
     * @param projectName         project name
     * @param pageNo              page number
     * @param pageSize            page size
     * @param processDefinitionId process definition id
     * @param searchVal           search value
     * @param stateType           state type
     * @param host                host
     * @param startTime           start time
     * @param endTime             end time
     * @return process instance list
     */


    @GetMapping(value = "list-paging")


    public Result queryProcessInstanceList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                           @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                                           @RequestParam(value = "searchVal", required = false) String searchVal,
                                           @RequestParam(value = "executorName", required = false) String executorName,
                                           @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                           @RequestParam(value = "host", required = false) String host,
                                           @RequestParam(value = "startDate", required = false) String startTime,
                                           @RequestParam(value = "endDate", required = false) String endTime,
                                           @RequestParam("pageNo") Integer pageNo,
                                           @RequestParam("pageSize") Integer pageSize) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query all process instance list, login user:{},project name:{}, define id:{}," +
                        "search value:{},executor name:{},state type:{},host:{},start time:{}, end time:{},page number:{}, page size:{}",
                loginUser.getUserName(), projectName, processDefinitionId, searchVal, executorName, stateType, host,
                startTime, endTime, pageNo, pageSize);
        searchVal = ParameterUtils.handleEscapes(searchVal);
        Map<String, Object> result = processInstanceService.queryProcessInstanceList(
                loginUser, projectName, processDefinitionId, startTime, endTime, searchVal, executorName, stateType, host, pageNo, pageSize);
        return returnDataListPaging(result);
    }

    /**
     * query task list by process instance id
     *
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @return task list for the process instance
     */


    @GetMapping(value = "/task-list-by-process-id")


    public Result queryTaskListByProcessId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                           @RequestParam("processInstanceId") Integer processInstanceId
    ) throws IOException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query task instance list by process instance id, login user:{}, project name:{}, process instance id:{}",
                loginUser.getUserName(), projectName, processInstanceId);
        Map<String, Object> result = processInstanceService.queryTaskListByProcessId(loginUser, projectName, processInstanceId);
        return returnDataList(result);
    }

    /**
     * update process instance
     *
     * @param projectName         project name
     * @param processInstanceJson process instance json
     * @param processInstanceId   process instance id
     * @param scheduleTime        schedule time
     * @param syncDefine          sync define
     * @param flag                flag
     * @param locations           locations
     * @param connects            connects
     * @return update result code
     */


    @PostMapping(value = "/update")


    public Result updateProcessInstance(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                        @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                        @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                        @RequestParam(value = "processInstanceJson", required = false) String processInstanceJson,
                                        @RequestParam(value = "processInstanceId") Integer processInstanceId,
                                        @RequestParam(value = "scheduleTime", required = false) String scheduleTime,
                                        @RequestParam(value = "syncDefine", required = true) Boolean syncDefine,
                                        @RequestParam(value = "locations", required = false) String locations,
                                        @RequestParam(value = "connects", required = false) String connects,
                                        @RequestParam(value = "flag", required = false) Flag flag
    ) throws ParseException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("updateProcessInstance process instance, login user:{}, project name:{}, process instance json:{}," +
                        "process instance id:{}, schedule time:{}, sync define:{}, flag:{}, locations:{}, connects:{}",
                loginUser.getUserName(), projectName, processInstanceJson, processInstanceId, scheduleTime,
                syncDefine, flag, locations, connects);
        Map<String, Object> result = processInstanceService.updateProcessInstance(loginUser, projectName,
                processInstanceId, processInstanceJson, scheduleTime, syncDefine, flag, locations, connects);
        return returnDataList(result);
    }

    /**
     * query process instance by id
     *
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @return process instance detail
     */


    @GetMapping(value = "/select-by-id")


    public Result queryProcessInstanceById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                           @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query process instance detail by id, login user:{},project name:{}, process instance id:{}",
                loginUser.getUserName(), projectName, processInstanceId);
        Map<String, Object> result = processInstanceService.queryProcessInstanceById(loginUser, projectName, processInstanceId);
        return returnDataList(result);
    }

    /**
     * delete process instance by id, at the same time,
     * delete task instance and their mapping relation data
     *
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @return delete result code
     */


    @GetMapping(value = "/delete")


    public Result deleteProcessInstanceById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                            @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                            @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("delete process instance by id, login user:{}, project name:{}, process instance id:{}",
                loginUser.getUserName(), projectName, processInstanceId);
        // task queue
        Map<String, Object> result = processInstanceService.deleteProcessInstanceById(loginUser, projectName, processInstanceId);
        return returnDataList(result);
    }

    /**
     * query sub process instance detail info by task id
     *
     * @param projectName project name
     * @param taskId      task id
     * @return sub process instance detail
     */


    @GetMapping(value = "/select-sub-process")


    public Result querySubProcessInstanceByTaskId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                  @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                  @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                                  @RequestParam("taskId") Integer taskId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        Map<String, Object> result = processInstanceService.querySubProcessInstanceByTaskId(loginUser, projectName, taskId);
        return returnDataList(result);
    }

    /**
     * query parent process instance detail info by sub process instance id
     *
     * @param projectName project name
     * @param subId       sub process id
     * @return parent instance detail
     */


    @GetMapping(value = "/select-parent-process")


    public Result queryParentInstanceBySubId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                             @RequestParam("subId") Integer subId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        Map<String, Object> result = processInstanceService.queryParentInstanceBySubId(loginUser, projectName, subId);
        return returnDataList(result);
    }

    /**
     * query process instance global variables and local variables
     *
     * @param processInstanceId process instance id
     * @return variables data
     */


    @GetMapping(value = "/view-variables")


    public Result viewVariables(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                @RequestParam("processInstanceId") Integer processInstanceId) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        Map<String, Object> result = processInstanceService.viewVariables(processInstanceId);
        return returnDataList(result);
    }

    /**
     * encapsulation gantt structure
     *
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @return gantt tree data
     */


    @GetMapping(value = "/view-gantt")


    public Result viewTree(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                           @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                           @RequestParam("processInstanceId") Integer processInstanceId) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        Map<String, Object> result = processInstanceService.viewGantt(processInstanceId);
        return returnDataList(result);
    }

    /**
     * batch delete process instance by ids, at the same time,
     * delete task instance and their mapping relation data
     *
     * @param projectName        project name
     * @param processInstanceIds process instance id
     * @return delete result code
     */
    @GetMapping(value = "/batch-delete")


    public Result batchDeleteProcessInstanceByIds(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                  @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                  @PathVariable String projectName,
                                                  @RequestParam("processInstanceIds") String processInstanceIds
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("delete process instance by ids, login user:{}, project name:{}, process instance ids :{}",
                loginUser.getUserName(), projectName, processInstanceIds);
        // task queue
        Map<String, Object> result = new HashMap<>(5);
        List<String> deleteFailedIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(processInstanceIds)) {
            String[] processInstanceIdArray = processInstanceIds.split(",");

            for (String strProcessInstanceId : processInstanceIdArray) {
                int processInstanceId = Integer.parseInt(strProcessInstanceId);
                try {
                    Map<String, Object> deleteResult = processInstanceService.deleteProcessInstanceById(loginUser, projectName, processInstanceId);
                    if (!Status.SUCCESS.equals(deleteResult.get(Constants.STATUS))) {
                        deleteFailedIdList.add(strProcessInstanceId);
                        logger.error((String) deleteResult.get(Constants.MSG));
                    }
                } catch (Exception e) {
                    deleteFailedIdList.add(strProcessInstanceId);
                }
            }
        }
        if (!deleteFailedIdList.isEmpty()) {
            putMsg(result, Status.BATCH_DELETE_PROCESS_INSTANCE_BY_IDS_ERROR, String.join(",", deleteFailedIdList));
        } else {
            putMsg(result, Status.SUCCESS);
        }

        return returnDataList(result);
    }
}

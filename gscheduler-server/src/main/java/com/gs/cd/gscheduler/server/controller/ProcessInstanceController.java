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


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.api.ProcessInstanceApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import com.gs.cd.gscheduler.server.entity.GschedulerProjectPurview;
import com.gs.cd.gscheduler.server.service.GschedulerProjectPurviewService;
import com.gs.cd.gscheduler.server.service.impl.PurviewCheckService;
import com.gs.cd.kmp.api.AuthClient;
import com.gs.cd.kmp.api.entity.Resource;
import com.gs.cd.kmp.api.enums.ResourceCategoryEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.common.enums.ExecutionStatus;
import org.apache.dolphinscheduler.common.enums.Flag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.gs.cd.gscheduler.server.Constant.ProcessInstancePerms;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流实例
 */
@RestController
@Slf4j
@RequestMapping("projects/{projectName}/instance")
public class ProcessInstanceController {

    @Autowired
    ProcessInstanceApi processInstanceApi;

    @Autowired
    PurviewCheckService purviewCheckService;


    /**
     * 工作流实例列表【分页】
     *
     * @param tenantCode          租户code
     * @param projectName         项目名称
     * @param processDefinitionId 工作流定义id
     * @param searchVal           搜索条件 名称
     * @param executorName
     * @param stateType
     * @param host
     * @param startTime           开始时间
     * @param endTime             结束时间
     * @param pageNo              当前页
     * @param pageSize            每页大小
     * @return
     */
    @GetMapping(value = "list-paging")
    public ApiResult queryProcessInstanceList(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam(value = "executorName", required = false) String executorName,
            @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
            @RequestParam(value = "host", required = false) String host,
            @RequestParam(value = "startDate", required = false) String startTime,
            @RequestParam(value = "endDate", required = false) String endTime,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam("pageSize") Integer pageSize) {
        purviewCheckService.check(projectName, ProcessInstancePerms.view, token, tenantCode);
        return processInstanceApi.queryProcessInstanceList(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId, searchVal, executorName, stateType, host, startTime, endTime, pageNo, pageSize).apiResult();
    }


    /**
     * @param tenantCode        租户code
     * @param projectName       项目名称
     * @param processInstanceId 工作流实例id
     * @return
     */
    @GetMapping(value = "/task-list-by-process-id")
    public ApiResult queryTaskListByProcessId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        return processInstanceApi.queryTaskListByProcessId(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceId).apiResult();
    }

    /**
     * 通过id 修改
     *
     * @param tenantCode          租户code
     * @param projectName         项目名称
     * @param processInstanceJson 定义json
     * @param processInstanceId   工作流实例id
     * @param scheduleTime
     * @param syncDefine
     * @param locations           位置
     * @param connects
     * @param flag
     * @return
     * @throws ParseException
     */
    @PostMapping(value = "/update")
    public ApiResult updateProcessInstance(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam(value = "processInstanceJson") String processInstanceJson,
            @RequestParam(value = "processInstanceId") Integer processInstanceId,
            @RequestParam(value = "scheduleTime", required = false) String scheduleTime,
            @RequestParam(value = "syncDefine", required = false, defaultValue = "true") Boolean syncDefine,
            @RequestParam(value = "locations") String locations,
            @RequestParam(value = "connects", required = false, defaultValue = "[]") String connects,
            @RequestParam(value = "flag", required = false) Flag flag
    ) throws ParseException {
        purviewCheckService.check(projectName, ProcessInstancePerms.edit, token, tenantCode);
        return processInstanceApi.updateProcessInstance(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceJson, processInstanceId, scheduleTime, syncDefine, locations, connects, flag).apiResult();
    }

    /**
     * 通过id查询
     *
     * @param tenantCode        租户code
     * @param projectName       项目名称
     * @param processInstanceId 工作流实例id
     * @return
     */
    @GetMapping(value = "/select-by-id")
    public ApiResult queryProcessInstanceById(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        return processInstanceApi.queryProcessInstanceById(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceId).apiResult();
    }

    /**
     * 通过id删除
     *
     * @param tenantCode        租户code
     * @param projectName       项目名称
     * @param processInstanceId 工作流实例id
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteProcessInstanceById(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        purviewCheckService.check(projectName, ProcessInstancePerms.delete, token, tenantCode);
        return processInstanceApi.deleteProcessInstanceById(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceId).apiResult();
    }

    /**
     * 未知
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param taskId
     * @return
     */
//    @GetMapping(value = "/select-sub-process")
    public ApiResult querySubProcessInstanceByTaskId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("taskId") Integer taskId) {
        return processInstanceApi.querySubProcessInstanceByTaskId(TenantCodeService.getSessionId(tenantCode),
                projectName, taskId).apiResult();
    }

    /**
     * 未知
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param subId
     * @return
     */
//    @GetMapping(value = "/select-parent-process")
    public ApiResult queryParentInstanceBySubId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("subId") Integer subId) {
        return processInstanceApi.queryParentInstanceBySubId(TenantCodeService.getSessionId(tenantCode),
                projectName, subId).apiResult();
    }

    //    @GetMapping(value = "/view-variables")
    public ApiResult viewVariables(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam("processInstanceId") Integer processInstanceId) throws Exception {
        return processInstanceApi.viewVariables(TenantCodeService.getSessionId(tenantCode),
                processInstanceId).apiResult();
    }

    /**
     * 查看甘特图
     *
     * @param tenantCode        租户code
     * @param projectName       项目名称
     * @param processInstanceId
     * @return
     * @throws Exception
     */
//    @GetMapping(value = "/view-gantt")
    public ApiResult viewTree(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processInstanceId") Integer processInstanceId) throws Exception {
        return processInstanceApi.viewTree(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceId).apiResult();
    }


    /**
     * 通过工作流实例批量删除
     *
     * @param tenantCode         租户code
     * @param projectName        项目名称
     * @param processInstanceIds 工作流实例id集合 eg:[876,869]
     * @return
     */
    @GetMapping(value = "/batch-delete")
    public ApiResult batchDeleteProcessInstanceByIds(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processInstanceIds") String processInstanceIds
    ) {
        return processInstanceApi.batchDeleteProcessInstanceByIds(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceIds).apiResult();
    }
}

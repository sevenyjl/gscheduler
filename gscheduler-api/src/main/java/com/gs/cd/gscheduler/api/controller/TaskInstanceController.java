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


import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.service.TaskInstanceService;
import com.gs.cd.gscheduler.api.utils.PageInfo;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.TaskInstance;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 任务实例管理
 */

@RestController
@Slf4j
@RequestMapping("/gscheduler/projects/{projectName}/task-instance")
public class TaskInstanceController {
    @Autowired
    TaskInstanceService taskInstanceService;

    /**
     * 分页查询
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceId
     * @param searchVal
     * @param taskName
     * @param executorName
     * @param stateType
     * @param host
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/list-paging")
    public ApiResult queryTaskListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                         @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                         @PathVariable String projectName,
                                         @RequestParam(value = "processInstanceId", required = false) Integer processInstanceId,
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
        log.info("query task instance list, project name:{},process instance:{}, search value:{},task name:{}, executor name: {},state type:{}, host:{}, start:{}, end:{}",
                projectName, processInstanceId, searchVal, taskName, executorName, stateType, host, startTime, endTime);
        QueryWrapper<TaskInstance> taskInstanceQueryWrapper = new QueryWrapper<>();
        if (processInstanceId != null) {
            taskInstanceQueryWrapper.lambda().eq(TaskInstance::getProcessInstanceId, processInstanceId);
        }
        if (searchVal != null) {
            taskInstanceQueryWrapper.lambda().like(TaskInstance::getName, "%" + searchVal + "%");
        }
        if (taskName != null) {
            taskInstanceQueryWrapper.lambda().eq(TaskInstance::getName, taskName);
        }
        if (executorName != null) {
            taskInstanceQueryWrapper.lambda().eq(TaskInstance::getExecutorName, executorName);
        }
        if (stateType != null) {
            taskInstanceQueryWrapper.lambda().eq(TaskInstance::getState, stateType);
        }
        if (host != null) {
            taskInstanceQueryWrapper.lambda().eq(TaskInstance::getHost, host);
        }
        if (startTime != null) {
            taskInstanceQueryWrapper.lambda().ge(TaskInstance::getStartTime, DateUtil.parse(startTime, Constants.YYYY_MM_DD_HH_MM_SS));
        }
        if (endTime != null) {
            taskInstanceQueryWrapper.lambda().le(TaskInstance::getStartTime, DateUtil.parse(endTime, Constants.YYYY_MM_DD_HH_MM_SS));
        }
        IPage<TaskInstance> page = taskInstanceService.page(new Page<>(pageNo, pageSize), taskInstanceQueryWrapper);
        return ApiResult.success(PageInfo.pageInfoTrans(page));
    }

}

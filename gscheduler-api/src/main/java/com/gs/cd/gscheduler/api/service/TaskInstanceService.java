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
package com.gs.cd.gscheduler.api.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.enums.Status;
import com.gs.cd.gscheduler.api.utils.PageInfo;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.utils.CollectionUtils;
import com.gs.cd.gscheduler.common.utils.DateUtils;
import com.gs.cd.gscheduler.common.utils.StringUtils;
import com.gs.cd.gscheduler.dao.entity.Project;
import com.gs.cd.gscheduler.dao.entity.TaskInstance;
import com.gs.cd.gscheduler.dao.mapper.ProjectMapper;
import com.gs.cd.gscheduler.dao.mapper.TaskInstanceMapper;
import com.gs.cd.gscheduler.service.process.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

/**
 * task instance service
 */
@Service
public class TaskInstanceService extends BaseService {

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    ProjectService projectService;

    @Autowired
    ProcessService processService;

    @Autowired
    TaskInstanceMapper taskInstanceMapper;

    @Autowired
    ProcessInstanceService processInstanceService;


    /**
     * query task list by project, process instance, task name, task start time, task end time, task status, keyword paging
     *
     * @param loginUser         login user
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @param searchVal         search value
     * @param taskName          task name
     * @param stateType         state type
     * @param host              host
     * @param startDate         start time
     * @param endDate           end time
     * @param pageNo            page number
     * @param pageSize          page size
     * @return task list page
     */
    public Map<String, Object> queryTaskListPaging(JwtUserInfo loginUser, String projectName,
                                                   Integer processInstanceId, String taskName, String executorName, String startDate,
                                                   String endDate, String searchVal, ExecutionStatus stateType, String host,
                                                   Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>(5);
        Project project = projectMapper.queryByName(projectName);

        Map<String, Object> checkResult = projectService.checkProjectAndAuth(loginUser, project, projectName);
        Status status = (Status) checkResult.get(Constants.STATUS);
        if (status != Status.SUCCESS) {
            return checkResult;
        }

        int[] statusArray = null;
        if (stateType != null) {
            statusArray = new int[]{stateType.ordinal()};
        }

        Date start = null;
        Date end = null;
        try {
            if (StringUtils.isNotEmpty(startDate)) {
                start = DateUtils.getScheduleDate(startDate);
            }
            if (StringUtils.isNotEmpty(endDate)) {
                end = DateUtils.getScheduleDate(endDate);
            }
        } catch (Exception e) {
            result.put(Constants.STATUS, Status.REQUEST_PARAMS_NOT_VALID_ERROR);
            result.put(Constants.MSG, MessageFormat.format(Status.REQUEST_PARAMS_NOT_VALID_ERROR.getMsg(), "startDate,endDate"));
            return result;
        }

        Page<TaskInstance> page = new Page(pageNo, pageSize);
        PageInfo pageInfo = new PageInfo<TaskInstance>(pageNo, pageSize);
        // TODO: 2021/5/7  executorId 执行者id
//        int executorId = usersService.getUserIdByName(executorName);
        int executorId = 0;

        IPage<TaskInstance> taskInstanceIPage = taskInstanceMapper.queryTaskInstanceListPaging(
                page, project.getId(), processInstanceId, searchVal, taskName, executorId, statusArray, host, start, end
        );
        Set<String> exclusionSet = new HashSet<>();
        exclusionSet.add(Constants.CLASS);
        exclusionSet.add("taskJson");
        List<TaskInstance> taskInstanceList = taskInstanceIPage.getRecords();

        for (TaskInstance taskInstance : taskInstanceList) {
            taskInstance.setDuration(DateUtils.differSec(taskInstance.getStartTime(), taskInstance.getEndTime()));
            // TODO: 2021/5/7 设置执行人名称
//            User executor = usersService.queryUser(taskInstance.getExecutorId());
//            if (null != executor) {
//                taskInstance.setExecutorName(executor.getUserName());
//            }
        }
        pageInfo.setTotalCount((int) taskInstanceIPage.getTotal());
        pageInfo.setLists(CollectionUtils.getListByExclusion(taskInstanceIPage.getRecords(), exclusionSet));
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }
}

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
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.service.DataAnalysisService;
import com.gs.cd.gscheduler.api.utils.Result;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;

/**
 * data analysis controller
 */

@RestController
@RequestMapping("projects/analysis")
public class DataAnalysisController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataAnalysisController.class);


    @Autowired
    DataAnalysisService dataAnalysisService;

    /**
     * statistical task instance status data
     *
     * @param startDate count start date
     * @param endDate   count end date
     * @param projectId project id
     * @return task instance count data
     */

    
    @GetMapping(value = "/task-state-count")


    public Result countTaskState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                 @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("count task state, user:{}, start date: {}, end date:{}, project id {}",
                loginUser.getUserName(), startDate, endDate, projectId);
        Map<String, Object> result = dataAnalysisService.countTaskStateByProject(loginUser, projectId, startDate, endDate);
        return returnDataList(result);
    }

    /**
     * statistical process instance status data
     *
     * @param startDate start date
     * @param endDate   end date
     * @param projectId project id
     * @return process instance data
     */

    
    @GetMapping(value = "/process-state-count")


    public Result countProcessInstanceState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                            @RequestParam(value = "startDate", required = false) String startDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("count process instance state, user:{}, start date: {}, end date:{}, project id:{}",
                loginUser.getUserName(), startDate, endDate, projectId);
        Map<String, Object> result = dataAnalysisService.countProcessInstanceStateByProject(loginUser, projectId, startDate, endDate);
        return returnDataList(result);
    }

    /**
     * statistics the process definition quantities of certain person
     *
     * @param projectId project id
     * @return definition count in project id
     */

    
    @GetMapping(value = "/define-user-count")


    public Result countDefinitionByUser(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                        @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                        @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("count process definition , user:{}, project id:{}",
                loginUser.getUserName(), projectId);
        Map<String, Object> result = dataAnalysisService.countDefinitionByUser(loginUser, projectId);
        return returnDataList(result);
    }


    /**
     * statistical command status data
     *
     * @param startDate start date
     * @param endDate   end date
     * @param projectId project id
     * @return command state in project id
     */

    
    @GetMapping(value = "/command-state-count")


    public Result countCommandState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate,
                                    @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("count command state, user:{}, start date: {}, end date:{}, project id {}",
                loginUser.getUserName(), startDate, endDate, projectId);
        Map<String, Object> result = dataAnalysisService.countCommandState(loginUser, projectId, startDate, endDate);
        return returnDataList(result);
    }

    /**
     * queue count
     *
     * @param projectId project id
     * @return queue state count
     */

    
    @GetMapping(value = "/queue-count")


    public Result countQueueState(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                  @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                  @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("count command state, user:{}, project id {}",
                loginUser.getUserName(), projectId);
        Map<String, Object> result = dataAnalysisService.countQueueState(loginUser, projectId);
        return returnDataList(result);
    }


}

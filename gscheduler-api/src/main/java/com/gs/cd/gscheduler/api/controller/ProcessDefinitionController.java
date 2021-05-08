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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.enums.Status;
import com.gs.cd.gscheduler.api.exceptions.ApiException;
import com.gs.cd.gscheduler.api.service.ProcessDefinitionService;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import com.gs.cd.gscheduler.common.utils.StringUtils;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;


/**
 * process definition controller
 */
@Api(tags = "PROCESS_DEFINITION_TAG", position = 2)
@RestController
@RequestMapping("projects/{projectName}/process")
public class ProcessDefinitionController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProcessDefinitionController.class);

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    /**
     * create process definition
     *
     * @param projectName project name
     * @param name        process definition name
     * @param json        process definition json
     * @param description description
     * @param locations   locations for nodes
     * @param connects    connects for nodes
     * @return create result code
     */

    @PostMapping(value = "/save")
    public Result createProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                          @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                          @RequestParam(value = "name", required = true) String name,
                                          @RequestParam(value = "processDefinitionJson", required = true) String json,
                                          @RequestParam(value = "locations", required = true) String locations,
                                          @RequestParam(value = "connects", required = true) String connects,
                                          @RequestParam(value = "description", required = false) String description) throws JsonProcessingException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, create  process definition, project name: {}, process definition name: {}, " +
                        "process_definition_json: {}, desc: {} locations:{}, connects:{}",
                loginUser.getUserName(), projectName, name, json, description, locations, connects);
        Map<String, Object> result = processDefinitionService.createProcessDefinition(loginUser, projectName, name, json,
                description, locations, connects);
        return returnDataList(result);
    }

    /**
     * copy process definition
     *
     * @param projectName project name
     * @param processId   process definition id
     * @return copy result code
     */

    @PostMapping(value = "/copy")
    public Result copyProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                        @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                        @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                        @RequestParam(value = "processId", required = true) int processId) throws JsonProcessingException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("copy process definition, login user:{}, project name:{}, process definition id:{}",
                loginUser.getUserName(), projectName, processId);
        Map<String, Object> result = processDefinitionService.copyProcessDefinition(loginUser, projectName, processId);
        return returnDataList(result);
    }

    /**
     * verify process definition name unique
     *
     * @param projectName project name
     * @param name        name
     * @return true if process definition name not exists, otherwise false
     */

    @GetMapping(value = "/verify-name")
    public Result verifyProcessDefinitionName(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                              @RequestParam(value = "name", required = true) String name) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("verify process definition name unique, user:{}, project name:{}, process definition name:{}",
                loginUser.getUserName(), projectName, name);
        Map<String, Object> result = processDefinitionService.verifyProcessDefinitionName(loginUser, projectName, name);
        return returnDataList(result);
    }

    /**
     * update process definition
     *
     * @param projectName           project name
     * @param name                  process definition name
     * @param id                    process definition id
     * @param processDefinitionJson process definition json
     * @param description           description
     * @param locations             locations for nodes
     * @param connects              connects for nodes
     * @return update result code
     */


    @PostMapping(value = "/update")
    public Result updateProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                          @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                          @RequestParam(value = "name", required = true) String name,
                                          @RequestParam(value = "id", required = true) int id,
                                          @RequestParam(value = "processDefinitionJson", required = true) String processDefinitionJson,
                                          @RequestParam(value = "locations", required = false) String locations,
                                          @RequestParam(value = "connects", required = false) String connects,
                                          @RequestParam(value = "description", required = false) String description) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, update process define, project name: {}, process define name: {}, " +
                        "process_definition_json: {}, desc: {}, locations:{}, connects:{}",
                loginUser.getUserName(), projectName, name, processDefinitionJson, description, locations, connects);
        Map<String, Object> result = processDefinitionService.updateProcessDefinition(loginUser, projectName, id, name,
                processDefinitionJson, description, locations, connects);
        return returnDataList(result);
    }

    /**
     * release process definition
     *
     * @param projectName  project name
     * @param processId    process definition id
     * @param releaseState release state
     * @return release result code
     */


    @PostMapping(value = "/release")
    public Result releaseProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                           @RequestParam(value = "processId", required = true) int processId,
                                           @RequestParam(value = "releaseState", required = true) int releaseState) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, release process definition, project name: {}, release state: {}",
                loginUser.getUserName(), projectName, releaseState);
        Map<String, Object> result = processDefinitionService.releaseProcessDefinition(loginUser, projectName, processId, releaseState);
        return returnDataList(result);
    }

    /**
     * query datail of process definition
     *
     * @param projectName project name
     * @param processId   process definition id
     * @return process definition detail
     */

    @GetMapping(value = "/select-by-id")
    public Result queryProcessDefinitionById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                             @RequestParam("processId") Integer processId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query detail of process definition, login user:{}, project name:{}, process definition id:{}",
                loginUser.getUserName(), projectName, processId);
        Map<String, Object> result = processDefinitionService.queryProcessDefinitionById(loginUser, projectName, processId);
        return returnDataList(result);
    }

    /**
     * query Process definition list
     *
     * @param projectName project name
     * @return process definition list
     */

    @GetMapping(value = "/list")
    public Result queryProcessDefinitionList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query process definition list, login user:{}, project name:{}",
                loginUser.getUserName(), projectName);
        Map<String, Object> result = processDefinitionService.queryProcessDefinitionList(loginUser, projectName);
        return returnDataList(result);
    }

    /**
     * query process definition list paging
     *
     * @param projectName project name
     * @param searchVal   search value
     * @param pageNo      page number
     * @param pageSize    page size
     * @param userId      user id
     * @return process definition page
     */

    @GetMapping(value = "/list-paging")
    public Result queryProcessDefinitionListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                   @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                   @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam(value = "searchVal", required = false) String searchVal,
                                                   @RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
                                                   @RequestParam("pageSize") Integer pageSize) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query process definition list paging, login user:{}, project name:{}", loginUser.getUserName(), projectName);
        Map<String, Object> result = checkPageParams(pageNo, pageSize);
        if (result.get(Constants.STATUS) != Status.SUCCESS) {
            return returnDataListPaging(result);
        }
        searchVal = ParameterUtils.handleEscapes(searchVal);
        result = processDefinitionService.queryProcessDefinitionListPaging(loginUser, projectName, searchVal, pageNo, pageSize, userId);
        return returnDataListPaging(result);
    }

    /**
     * encapsulation treeview structure
     *
     * @param projectName project name
     * @param id          process definition id
     * @param limit       limit
     * @return tree view json data
     */

    @GetMapping(value = "/view-tree")
    public Result viewTree(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                           @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                           @RequestParam("processId") Integer id,
                           @RequestParam("limit") Integer limit) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        Map<String, Object> result = processDefinitionService.viewTree(id, limit);
        return returnDataList(result);
    }

    /**
     * get tasks list by process definition id
     *
     * @param projectName         project name
     * @param processDefinitionId process definition id
     * @return task list
     */

    @GetMapping(value = "gen-task-list")
    public Result getNodeListByDefinitionId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
            @RequestParam("processDefinitionId") Integer processDefinitionId) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query task node name list by definitionId, login user:{}, project name:{}, id : {}",
                loginUser.getUserName(), projectName, processDefinitionId);
        Map<String, Object> result = processDefinitionService.getTaskNodeListByDefinitionId(processDefinitionId);
        return returnDataList(result);
    }

    /**
     * get tasks list by process definition id
     *
     * @param projectName             project name
     * @param processDefinitionIdList process definition id list
     * @return node list data
     */

    @GetMapping(value = "get-task-list")
    public Result getNodeListByDefinitionIdList(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
            @RequestParam("processDefinitionIdList") String processDefinitionIdList) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);

        logger.info("query task node name list by definitionId list, login user:{}, project name:{}, id list: {}",
                loginUser.getUserName(), projectName, processDefinitionIdList);
        Map<String, Object> result = processDefinitionService.getTaskNodeListByDefinitionIdList(processDefinitionIdList);
        return returnDataList(result);
    }

    /**
     * delete process definition by id
     *
     * @param projectName         project name
     * @param processDefinitionId process definition id
     * @return delete result code
     */

    @GetMapping(value = "/delete")
    public Result deleteProcessDefinitionById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                              @RequestParam("processDefinitionId") Integer processDefinitionId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("delete process definition by id, login user:{}, project name:{}, process definition id:{}",
                loginUser.getUserName(), projectName, processDefinitionId);
        Map<String, Object> result = processDefinitionService.deleteProcessDefinitionById(loginUser, projectName, processDefinitionId);
        return returnDataList(result);
    }

    /**
     * batch delete process definition by ids
     *
     * @param projectName          project name
     * @param processDefinitionIds process definition id list
     * @return delete result code
     */

    @GetMapping(value = "/batch-delete")
    public Result batchDeleteProcessDefinitionByIds(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                    @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                    @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                                    @RequestParam("processDefinitionIds") String processDefinitionIds
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("delete process definition by ids, login user:{}, project name:{}, process definition ids:{}",
                loginUser.getUserName(), projectName, processDefinitionIds);

        Map<String, Object> result = new HashMap<>(5);
        List<String> deleteFailedIdList = new ArrayList<>();
        if (StringUtils.isNotEmpty(processDefinitionIds)) {
            String[] processDefinitionIdArray = processDefinitionIds.split(",");

            for (String strProcessDefinitionId : processDefinitionIdArray) {
                int processDefinitionId = Integer.parseInt(strProcessDefinitionId);
                try {
                    Map<String, Object> deleteResult = processDefinitionService.deleteProcessDefinitionById(loginUser, projectName, processDefinitionId);
                    if (!Status.SUCCESS.equals(deleteResult.get(Constants.STATUS))) {
                        deleteFailedIdList.add(strProcessDefinitionId);
                        logger.error((String) deleteResult.get(Constants.MSG));
                    }
                } catch (Exception e) {
                    deleteFailedIdList.add(strProcessDefinitionId);
                }
            }
        }

        if (!deleteFailedIdList.isEmpty()) {
            putMsg(result, Status.BATCH_DELETE_PROCESS_DEFINE_BY_IDS_ERROR, String.join(",", deleteFailedIdList));
        } else {
            putMsg(result, Status.SUCCESS);
        }

        return returnDataList(result);
    }

    /**
     * batch export process definition by ids
     *
     * @param projectName          project name
     * @param processDefinitionIds process definition ids
     * @param response             response
     */


    @GetMapping(value = "/export")
    public void batchExportProcessDefinitionByIds(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                  @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                  @ApiParam(name = "projectName", value = "PROJECT_NAME", required = true) @PathVariable String projectName,
                                                  @RequestParam("processDefinitionIds") String processDefinitionIds,
                                                  HttpServletResponse response) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        try {
            logger.info("batch export process definition by ids, login user:{}, project name:{}, process definition ids:{}",
                    loginUser.getUserName(), projectName, processDefinitionIds);
            processDefinitionService.batchExportProcessDefinitionByIds(loginUser, projectName, processDefinitionIds, response);
        } catch (Exception e) {
            logger.error(Status.BATCH_EXPORT_PROCESS_DEFINE_BY_IDS_ERROR.getMsg(), e);
        }
    }

    /**
     * query process definition all by project id
     *
     * @param projectId project id
     * @return process definition list
     */
    @GetMapping(value = "/queryProcessDefinitionAllByProjectId")
    public Result queryProcessDefinitionAllByProjectId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                       @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                       @RequestParam("projectId") Integer projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("query process definition list, login user:{}, project id:{}",
                loginUser.getUserName(), projectId);
        Map<String, Object> result = processDefinitionService.queryProcessDefinitionAllByProjectId(projectId);
        return returnDataList(result);
    }

}

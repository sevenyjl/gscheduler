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
import com.gs.cd.gscheduler.api.service.ProcessDefinitionService;
import com.gs.cd.gscheduler.api.service.ProjectService;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.gs.cd.gscheduler.api.enums.Status.*;

/**
 * project controller
 */
@Api(tags = "PROJECT_TAG", position = 1)
@RestController
@RequestMapping("projects")
public class ProjectController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProcessDefinitionService processDefinitionService;

    /**
     * create project
     *
     * @param projectName project name
     * @param description description
     * @return returns an error if it exists
     */
    @PostMapping(value = "/create")
    public Result createProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                @RequestParam("projectName") String projectName,
                                @RequestParam(value = "description", required = false) String description) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, create project name: {}, desc: {}", loginUser.getUserName(), projectName, description);
        Map<String, Object> result = projectService.createProject(loginUser, projectName, description);
        return returnDataList(result);
    }

    /**
     * updateProcessInstance project
     *
     * @param projectId   project id
     * @param projectName project name
     * @param description description
     * @return update result code
     */
    @PostMapping(value = "/update")
    public Result updateProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                @RequestParam("projectId") Integer projectId,
                                @RequestParam("projectName") String projectName,
                                @RequestParam(value = "description", required = false) String description) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {} , updateProcessInstance project name: {}, desc: {}", loginUser.getUserName(), projectName, description);
        Map<String, Object> result = projectService.update(loginUser, projectId, projectName, description);
        return returnDataList(result);
    }

    /**
     * query project details by id
     *
     * @param projectId project id
     * @return project detail information
     */

    @GetMapping(value = "/query-by-id")
    public Result queryProjectById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                   @RequestParam("projectId") Integer projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query project by id: {}", loginUser.getUserName(), projectId);

        Map<String, Object> result = projectService.queryById(projectId);
        return returnDataList(result);
    }

    /**
     * query project list paging
     *
     * @param searchVal search value
     * @param pageSize  page size
     * @param pageNo    page number
     * @return project list which the login user have permission to see
     */

    @GetMapping(value = "/list-paging")
    public Result queryProjectListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                         @RequestParam(value = "searchVal", required = false) String searchVal,
                                         @RequestParam("pageSize") Integer pageSize,
                                         @RequestParam("pageNo") Integer pageNo
    ) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query project list paging", loginUser.getUserName());
        searchVal = ParameterUtils.handleEscapes(searchVal);
        Map<String, Object> result = projectService.queryProjectListPaging(loginUser, pageSize, pageNo, searchVal);
        return returnDataListPaging(result);
    }

    /**
     * delete project by id
     *
     * @param projectId project id
     * @return delete result code
     */

    @GetMapping(value = "/delete")
    public Result deleteProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                @RequestParam("projectId") Integer projectId
    ) {

        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, delete project: {}.", loginUser.getUserName(), projectId);
        Map<String, Object> result = projectService.deleteProject(loginUser, projectId);
        return returnDataList(result);
    }

    /**
     * query unauthorized project
     *
     * @param userId    user id
     * @return the projects which user have not permission to see
     */

    @GetMapping(value = "/unauth-project")
    public Result queryUnauthorizedProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @RequestParam("userId") Integer userId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query unauthorized project by user id: {}.", loginUser.getUserName(), userId);
        Map<String, Object> result = projectService.queryUnauthorizedProject(loginUser, userId);
        return returnDataList(result);
    }


    /**
     * query authorized project
     *
     * @param userId    user id
     * @return projects which the user have permission to see, Except for items created by this user
     */

    @GetMapping(value = "/authed-project")
    public Result queryAuthorizedProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                         @RequestParam("userId") Integer userId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query authorized project by user id: {}.", loginUser.getUserName(), userId);
        Map<String, Object> result = projectService.queryAuthorizedProject(loginUser, userId);
        return returnDataList(result);
    }

    /**
     * import process definition
     *
     * @param file        resource file
     * @param projectName project name
     * @return import result code
     */


    @PostMapping(value = "/import-definition")
    public Result importProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam("projectName") String projectName) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("import process definition by id, login user:{}, project: {}",
                loginUser.getUserName(), projectName);
        Map<String, Object> result = processDefinitionService.importProcessDefinition(loginUser, file, projectName);
        return returnDataList(result);
    }

    /**
     * query all project list
     *
     * @return all project list
     */
    @GetMapping(value = "/query-project-list")
    public Result queryAllProjectList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        logger.info("login user {}, query all project list", loginUser.getUserName());
        Map<String, Object> result = projectService.queryAllProjectList();
        return returnDataList(result);
    }


}

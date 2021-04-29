package com.gs.cd.gscheduler.api.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.service.ProcessDefinitionService;
import com.gs.cd.gscheduler.common.entity.ProcessDefinition;
import com.gs.cd.gscheduler.common.model.TaskNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;


/**
 * 工作流定义管理
 *
 * @Author seven
 * @Date 2021/4/23 16:01
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("/gscheduler/projects/{projectName}/process")
@Log4j2
public class ProcessDefinitionController {

    @Autowired
    ProcessDefinitionService processDefinitionService;

    /**
     * 保存工作流实例
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param name
     * @param json
     * @param locations
     * @param connects
     * @param description
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/save")
    public ApiResult createProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @PathVariable String projectName,
                                             @RequestParam(value = "name", required = true) String name,
                                             @RequestParam(value = "processDefinitionJson", required = true) String json,
                                             @RequestParam(value = "locations", required = true) String locations,
                                             @RequestParam(value = "connects", required = true) String connects,
                                             @RequestParam(value = "description", required = false) String description) throws JsonProcessingException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, create  process definition, project name: {}, process definition name: {}, " +
                        "process_definition_json: {}, desc: {} locations:{}, connects:{}",
                loginUser.getUserName(), projectName, name, json, description, locations, connects);
        boolean b = processDefinitionService.createProcessDefinition(loginUser, projectName, name, json,
                description, locations, connects);
        return b ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 复制工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processId
     * @return
     * @throws JsonProcessingException
     */
    @PostMapping(value = "/copy")
    public ApiResult copyProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @PathVariable String projectName,
                                           @RequestParam(value = "processId", required = true) int processId) throws JsonProcessingException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("copy process definition, login user:{}, project name:{}, process definition id:{}",
                loginUser.getUserName(), projectName, processId);
        boolean b = processDefinitionService.copyProcessDefinition(loginUser, projectName, processId);
        return b ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 校验名称有效性
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param name
     * @return
     */
    @GetMapping(value = "/verify-name")
    public ApiResult verifyProcessDefinitionName(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                 @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                 @PathVariable String projectName,
                                                 @RequestParam(value = "name", required = true) String name) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("verify process definition name unique, user:{}, project name:{}, process definition name:{}",
                loginUser.getUserName(), projectName, name);
        return processDefinitionService.verifyProcessDefinitionName(loginUser, projectName, name);
    }


    /**
     * 更新工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param name
     * @param id
     * @param processDefinitionJson
     * @param locations
     * @param connects
     * @param description
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResult updateProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @PathVariable String projectName,
                                             @RequestParam(value = "name", required = true) String name,
                                             @RequestParam(value = "id", required = true) int id,
                                             @RequestParam(value = "processDefinitionJson", required = true) String processDefinitionJson,
                                             @RequestParam(value = "locations", required = false) String locations,
                                             @RequestParam(value = "connects", required = false) String connects,
                                             @RequestParam(value = "description", required = false) String description) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, update process define, project name: {}, process define name: {}, " +
                        "process_definition_json: {}, desc: {}, locations:{}, connects:{}",
                loginUser.getUserName(), projectName, name, processDefinitionJson, description, locations, connects);
        boolean b = processDefinitionService.updateProcessDefinition(loginUser, projectName, id, name,
                processDefinitionJson, description, locations, connects);
        return b ? ApiResult.success() : ApiResult.error();
    }


    /**
     * 发布工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processId
     * @param releaseState
     * @return
     */
    @PostMapping(value = "/release")
    public ApiResult releaseProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @PathVariable String projectName,
                                              @RequestParam(value = "processId", required = true) int processId,
                                              @RequestParam(value = "releaseState", required = true) int releaseState) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, release process definition, project name: {}, release state: {}",
                loginUser.getUserName(), projectName, releaseState);
//        boolean b = processDefinitionService.releaseProcessDefinition(loginUser, projectName, processId, releaseState);
        return ApiResult.error("release 未开发");

    }


    /**
     * 通过id搜索
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processId
     * @return
     */
    @GetMapping(value = "/select-by-id")
    public ApiResult queryProcessDefinitionById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                @PathVariable String projectName,
                                                @RequestParam("processId") Integer processId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query detail of process definition, login user:{}, project name:{}, process definition id:{}",
                loginUser.getUserName(), projectName, processId);
        ProcessDefinition byId = processDefinitionService.getById(processId);
        return ApiResult.success(byId);
    }

    /**
     * 所有工作流定义
     * 项目下的所有工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @return
     */
    @GetMapping(value = "/list")
    public ApiResult queryProcessDefinitionList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                @PathVariable String projectName
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query process definition list, login user:{}, project name:{}",
                loginUser.getUserName(), projectName);
        List<ProcessDefinition> list = processDefinitionService.listByProjectName(projectName);
        return ApiResult.success(list);
    }

    /**
     * 分页查询
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param pageNo
     * @param searchVal
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/list-paging")
    public ApiResult queryProcessDefinitionListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                      @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                      @PathVariable String projectName,
                                                      @RequestParam("pageNo") Integer pageNo,
                                                      @RequestParam(value = "searchVal", required = false) String searchVal,
                                                      @RequestParam("pageSize") Integer pageSize) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query process definition list paging, login user:{}, project name:{}", loginUser.getUserName(), projectName);
        LambdaQueryWrapper<ProcessDefinition> eq = new QueryWrapper<ProcessDefinition>().lambda()
                .eq(ProcessDefinition::getProjectName, projectName);
        if (StrUtil.isNotEmpty(searchVal)) {
            eq.like(ProcessDefinition::getName, "%" + searchVal + "%");
        }
        return ApiResult.success(processDefinitionService.page(new Page<>(pageNo, pageSize), eq));
    }

    /**
     * view-tree
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param id
     * @param limit
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/view-tree")
    public ApiResult viewTree(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                              @PathVariable String projectName,
                              @RequestParam("processId") Integer id,
                              @RequestParam("limit") Integer limit) throws Exception {
//         processDefinitionService.viewTree(id, limit);
        return ApiResult.error("未开发");
    }


    /**
     * gen-task-list
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processDefinitionId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "gen-task-list")
    public ApiResult getNodeListByDefinitionId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam("processDefinitionId") Integer processDefinitionId) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query task node name list by definitionId, login user:{}, project name:{}, id : {}",
                loginUser.getUserName(), projectName, processDefinitionId);
        List<TaskNode> taskNodeList = processDefinitionService.getTaskNodeListByDefinitionId(processDefinitionId);
        return ApiResult.success(taskNodeList);
    }


    /**
     * get-task-list
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processDefinitionIdList
     * @return
     * @throws Exception
     */
    @GetMapping(value = "get-task-list")
    public ApiResult getNodeListByDefinitionIdList(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam("processDefinitionIdList") String processDefinitionIdList) throws Exception {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query task node name list by definitionId list, login user:{}, project name:{}, id list: {}",
                loginUser.getUserName(), projectName, processDefinitionIdList);
        Map<Integer, List<TaskNode>> map = processDefinitionService.getTaskNodeListByDefinitionIdList(processDefinitionIdList);
        return ApiResult.success(map);
    }

    /**
     * 删除工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processDefinitionId
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteProcessDefinitionById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                 @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                 @PathVariable String projectName,
                                                 @RequestParam("processDefinitionId") Integer processDefinitionId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("delete process definition by id, login user:{}, project name:{}, process definition id:{}",
                loginUser.getUserName(), projectName, processDefinitionId);
        boolean b = processDefinitionService.removeById(processDefinitionId);
        return b ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 批量删除
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processDefinitionIds
     * @return
     */
    @GetMapping(value = "/batch-delete")
    public ApiResult batchDeleteProcessDefinitionByIds(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                       @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                       @PathVariable String projectName,
                                                       @RequestParam("processDefinitionIds") String processDefinitionIds
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("delete process definition by ids, login user:{}, project name:{}, process definition ids:{}",
                loginUser.getUserName(), projectName, processDefinitionIds);
        if (StrUtil.isNotEmpty(processDefinitionIds)) {
            boolean b = processDefinitionService.removeByIds(Arrays.asList(processDefinitionIds.split(",").clone()));
            return b ? ApiResult.success() : ApiResult.error();
        }
        return ApiResult.error();
    }


    /**
     * 导出工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processDefinitionIds
     * @param response
     */
    @GetMapping(value = "/export")
    public void batchExportProcessDefinitionByIds(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                  @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                  @PathVariable String projectName,
                                                  @RequestParam("processDefinitionIds") String processDefinitionIds,
                                                  HttpServletResponse response) {
        try {
            JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
            log.info("batch export process definition by ids, login user:{}, project name:{}, process definition ids:{}",
                    loginUser.getUserName(), projectName, processDefinitionIds);
            processDefinitionService.batchExportProcessDefinitionByIds(projectName, processDefinitionIds, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过项目id查询 项目拥有的工作流定义
     *
     * @param tenantCode
     * @param token
     * @param projectId
     * @return
     */
    @GetMapping(value = "/queryProcessDefinitionAllByProjectId")
    public ApiResult queryProcessDefinitionAllByProjectId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                          @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                          @RequestParam("projectId") Integer projectId) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query process definition list, login user:{}, project id:{}",
                loginUser.getUserName(), projectId);
        List<ProcessDefinition> list = processDefinitionService.list(new QueryWrapper<ProcessDefinition>().lambda().eq(ProcessDefinition::getProjectId, projectId));
        return ApiResult.success(list);
    }

}

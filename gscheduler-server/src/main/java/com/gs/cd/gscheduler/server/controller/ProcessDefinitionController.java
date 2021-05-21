package com.gs.cd.gscheduler.server.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSON;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.ProcessDefinitionApi;
import com.gs.cd.gscheduler.api.ProcessInstanceApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * 工作流定义管理
 *
 * @Author seven
 * @Date 2021/4/14 9:51
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("projects/{projectName}/process")
@Slf4j
public class ProcessDefinitionController {
    @Autowired
    ProcessDefinitionApi processDefinitionApi;


    /**
     * 创建process定义
     * <p>
     * shell的processDefinitionJson
     * <p>
     * ```json
     * {"globalParams":[],"tasks":[{"type":"SHELL","id":"tasks-26639","name":"简单shell","params":{"resourceList":[],"localParams":[],"rawScript":"echo \"这是内容\""},"description":"这是描述","timeout":{"strategy":"","interval":null,"enable":false},"runFlag":"NORMAL","conditionResult":{"successNode":[""],"failedNode":[""]},"dependence":{},"maxRetryTimes":"0","retryInterval":"1","taskInstancePriority":"MEDIUM","workerGroup":"default","preTasks":[]}],"tenantId":4,"timeout":0}
     * ```
     * <p>
     * 位置locations参数
     * ```json
     * {"tasks-26639":{"name":"简单shell","targetarr":"","nodenumber":"0","x":194,"y":60}}
     * ```
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param name        工作流名称
     * @param json
     * @param locations
     * @param connects
     * @param description 描述
     * @return
     */
    @PostMapping("/save")
    public ApiResult createProcessDefinition(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "processDefinitionJson", required = true) String json,
            @RequestParam(value = "locations", required = true) String locations,
            @RequestParam(value = "connects", required = true) String connects,
            @RequestParam(value = "description", required = false) String description
    ) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        return processDefinitionApi.createProcessDefinition(TenantCodeService.getSessionId(tenantCode),
                projectName, name, json, locations, connects, description, jwtUserInfo.getUserName()).apiResult();
    }

    /**
     * 通过id复制工作流定义
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param processId   工作流id
     * @return
     */
    @PostMapping(value = "/copy")
    public ApiResult copyProcessDefinition(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam(value = "processId", required = true) int processId
    ) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        return processDefinitionApi.copyProcessDefinition(TenantCodeService.getSessionId(tenantCode),
                projectName, processId, jwtUserInfo.getUserName()).apiResult();
    }

    /**
     * 验证工作流名称
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param name        工作流名称
     * @return
     */
    @GetMapping(value = "/verify-name")
    public ApiResult verifyProcessDefinitionName(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam(value = "name", required = true) String name) {
        return processDefinitionApi.verifyProcessDefinitionName(TenantCodeService.getSessionId(tenantCode),
                projectName, name).apiResult();
    }

    /**
     * 更新工作流
     *
     * @param tenantCode            租户code
     * @param projectName           项目名称
     * @param name                  工作流名称
     * @param id                    工作流id
     * @param processDefinitionJson
     * @param locations
     * @param connects
     * @param description           工作流描述
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResult updateProcessDefinition(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @PathVariable String projectName,
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "id", required = true) int id,
            @RequestParam(value = "processDefinitionJson", required = true) String processDefinitionJson,
            @RequestParam(value = "locations", required = false) String locations,
            @RequestParam(value = "connects", required = false) String connects,
            @RequestParam(value = "description", required = false) String description) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        return processDefinitionApi.updateProcessDefinition(TenantCodeService.getSessionId(tenantCode),
                projectName, name, id, processDefinitionJson, locations, connects, description, jwtUserInfo.getUserName()).apiResult();
    }


    /**
     * 上线工作流
     *
     * @param tenantCode   租户code
     * @param projectName  项目名称
     * @param processId    工作流id
     * @param releaseState 发布状态【0为下线、1为上线】
     * @return
     */
    @PostMapping(value = "/release")
    public ApiResult releaseProcessDefinition(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam(value = "processId", required = true) int processId,
            @RequestParam(value = "releaseState", required = true) int releaseState) {
        return processDefinitionApi.releaseProcessDefinition(TenantCodeService.getSessionId(tenantCode),
                projectName, processId, releaseState).apiResult();
    }

    /**
     * 通过id查询
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param processId   工作流id
     * @return
     */
    @GetMapping(value = "/select-by-id")
    public ApiResult queryProcessDefinitionById(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processId") Integer processId
    ) {
        return processDefinitionApi.queryProcessDefinitionById(TenantCodeService.getSessionId(tenantCode),
                projectName, processId).apiResult();
    }


    /**
     * 获取工作流定义列表
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @return
     */
    @GetMapping(value = "/list")
    public ApiResult queryProcessDefinitionList(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName
    ) {
        return processDefinitionApi.queryProcessDefinitionList(TenantCodeService.getSessionId(tenantCode),
                projectName).apiResult();
    }

    /**
     * 获取工作流定义列表【分页】
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param pageNo      当前页
     * @param searchVal   名称搜索条件
     * @param userId      用户id
     *                    todo userId 应该没有
     * @param pageSize    每页大小
     * @return
     */
    @GetMapping(value = "/list-paging")
    public ApiResult queryProcessDefinitionListPaging(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("pageNo") Integer pageNo,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam(value = "userId", required = false, defaultValue = "0") Integer userId,
            @RequestParam("pageSize") Integer pageSize) {
        return processDefinitionApi.queryProcessDefinitionListPaging(TenantCodeService.getSessionId(tenantCode),
                projectName, pageNo, pageSize, searchVal, userId).apiResult();
    }


    //    @GetMapping(value = "/view-tree")
    public ApiResult viewTree(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processId") Integer id,
            @RequestParam("limit") Integer limit) {
        return processDefinitionApi.viewTree(TenantCodeService.getSessionId(tenantCode),
                projectName, id, limit).apiResult();
    }

    /**
     * 通过工作流定义ID获取任务列表
     *
     * @param tenantCode          租户code
     * @param projectName         项目名称
     * @param processDefinitionId 工作流
     * @return
     */
    @GetMapping(value = "gen-task-list")
    public ApiResult getNodeListByDefinitionId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processDefinitionId") Integer processDefinitionId) {
        return processDefinitionApi.getNodeListByDefinitionId(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId).apiResult();
    }

    /**
     * 通过工作流定义ID列表获取任务列表
     *
     * @param tenantCode              租户code
     * @param projectName             项目名称
     * @param processDefinitionIdList 工作流id列表
     * @return
     * @throws Exception
     */
    @GetMapping(value = "get-task-list")
    public ApiResult getNodeListByDefinitionIdList(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processDefinitionIdList") String processDefinitionIdList) throws Exception {
        return processDefinitionApi.getNodeListByDefinitionIdList(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionIdList).apiResult();
    }

    /**
     * 通过工作流id删除
     *
     * @param tenantCode          租户code
     * @param projectName         项目名称
     * @param processDefinitionId 工作流id
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteProcessDefinitionById(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processDefinitionId") Integer processDefinitionId
    ) {
        return processDefinitionApi.deleteProcessDefinitionById(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId).apiResult();
    }


    /**
     * 通过工作流id批量删除
     *
     * @param tenantCode           租户code
     * @param projectName          项目名称
     * @param processDefinitionIds 工作流id列表
     * @return
     */
    @GetMapping(value = "/batch-delete")
    public ApiResult batchDeleteProcessDefinitionByIds(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processDefinitionIds") String processDefinitionIds
    ) {
        return processDefinitionApi.batchDeleteProcessDefinitionByIds(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionIds).apiResult();
    }

    /**
     * 导出
     *
     * @param tenantCode           租户code
     * @param projectName          项目名称
     * @param processDefinitionIds 工作流id列表
     */
    @GetMapping(value = "/export")
    public void batchExportProcessDefinitionByIds(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processDefinitionIds") String processDefinitionIds,
            HttpServletResponse response) throws IOException {
        String r = processDefinitionApi.batchExportProcessDefinitionByIds(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionIds);
        ServletOutputStream outputStream = null;
        try {
            response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + projectName + "_" + tenantCode + DateUtil.format(new Date(), Constants.PARAMETER_FORMAT_TIME) + ".json");
            outputStream = response.getOutputStream();
            outputStream.write(r.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.close(outputStream);
        }
    }

    /**
     * 通过项目id查询所有工作流定义
     *
     * @param tenantCode 租户code
     * @param projectId  项目id
     * @return
     */
    @GetMapping(value = "/queryProcessDefinitionAllByProjectId")
    public ApiResult queryProcessDefinitionAllByProjectId(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam("projectId") Integer projectId) {
        return processDefinitionApi.queryProcessDefinitionAllByProjectId(TenantCodeService.getSessionId(tenantCode),
                projectId).apiResult();
    }


}

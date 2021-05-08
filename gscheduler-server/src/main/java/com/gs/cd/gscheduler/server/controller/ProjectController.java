package com.gs.cd.gscheduler.server.controller;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.api.ProjectApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 项目管理
 *
 * @Author seven
 * @Date 2021/4/13 17:06
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("/projects")
public class ProjectController {
    @Autowired
    ProjectApi projectApi;

    /**
     * 创建项目
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param description
     * @return
     */
    @PostMapping("/create")
    public ApiResult createProject(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam("projectName") String projectName,
            @RequestParam(value = "description", required = false) String description) {
        String sessionId = TenantCodeService.getSessionId(tenantCode);
        return projectApi.createProject(sessionId, projectName, description).apiResult();
    }

    /**
     * 更新项目
     *
     * @param tenantCode  租户code
     * @param projectId   项目id
     * @param projectName 项目名称
     * @param description 描述
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResult updateProject(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam("projectId") Integer projectId,
            @RequestParam("projectName") String projectName,
            @RequestParam(value = "description", required = false) String description) {
        String sessionId = TenantCodeService.getSessionId(tenantCode);
        return projectApi.updateProject(sessionId, projectId, projectName, description).apiResult();
    }

    /**
     * 通过id查询项目详情
     *
     * @param tenantCode 租户code
     * @param projectId  项目id
     * @return
     */
    @PostMapping(value = "/query-by-id")
    public ApiResult queryProjectById(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam("projectId") Integer projectId) {
        String sessionId = TenantCodeService.getSessionId(tenantCode);
        return projectApi.queryProjectById(sessionId, projectId).apiResult();
    }

    /**
     * 分页查询
     *
     * @param tenantCode 租户code
     * @param searchVal  搜索条件 项目名称
     * @param pageSize   每页大小
     * @param pageNo     当前页
     * @return
     */
    @GetMapping(value = "/list-paging")
    public ApiResult queryProjectListPaging(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam("pageNo") Integer pageNo
    ) {
        return projectApi.queryProjectListPaging(TenantCodeService.getSessionId(tenantCode),
                searchVal, pageSize, pageNo).apiResult();
    }

    /**
     * 通过id
     *
     * @param tenantCode 租户code
     * @param projectId  项目id
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteProject(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam("projectId") Integer projectId
    ) {
        return projectApi.deleteProject(TenantCodeService.getSessionId(tenantCode), projectId).apiResult();
    }
}

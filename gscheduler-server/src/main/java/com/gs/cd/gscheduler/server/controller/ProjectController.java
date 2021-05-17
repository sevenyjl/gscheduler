package com.gs.cd.gscheduler.server.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.ProjectApi;
import com.gs.cd.gscheduler.entity.Project;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import com.gs.cd.gscheduler.server.entity.GschedulerProjectPurview;
import com.gs.cd.gscheduler.server.service.GschedulerProjectPurviewService;
import com.gs.cd.gscheduler.server.vo.UserGroupAndRoleVO;
import com.gs.cd.gscheduler.utils.Result;
import com.gs.cd.kmp.api.entity.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
@Slf4j
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
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @RequestParam("projectName") String projectName,
            @RequestParam(value = "description", required = false) String description) {
        String sessionId = TenantCodeService.getSessionId(tenantCode);
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
//        check(GschedulerProjectPurview.add, jwtUserInfo);
        return projectApi.createProject(sessionId, projectName, description, jwtUserInfo.getUserName()).apiResult();
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
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @RequestParam("projectId") Integer projectId,
            @RequestParam("projectName") String projectName,
            @RequestParam(value = "description", required = false) String description) {
        String sessionId = TenantCodeService.getSessionId(tenantCode);
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        check(projectId, GschedulerProjectPurview.edit, token, tenantCode);
        return projectApi.updateProject(sessionId, projectId, projectName, description, jwtUserInfo.getUserName()).apiResult();
    }

    /**
     * 通过id查询项目详情
     *
     * @param tenantCode 租户code
     * @param projectId  项目id
     * @return
     */
    @GetMapping(value = "/query-by-id")
    public ApiResult queryProjectById(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @RequestParam("projectId") Integer projectId) {
        String sessionId = TenantCodeService.getSessionId(tenantCode);
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
//        check(GschedulerProjectPurview.view, jwtUserInfo);
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
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @RequestParam(value = "searchVal", required = false) String searchVal,
            @RequestParam("pageSize") Integer pageSize,
            @RequestParam("pageNo") Integer pageNo
    ) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
//        check(GschedulerProjectPurview.view, jwtUserInfo);
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
            @RequestHeader(HttpHeadersParam.TOKEN) String token,
            @RequestParam("projectId") Integer projectId
    ) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        check(projectId, GschedulerProjectPurview.delete, token, tenantCode);
        return projectApi.deleteProject(TenantCodeService.getSessionId(tenantCode), projectId).apiResult();
    }


    @PostMapping(value = "/import-definition")
    public ApiResult importProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("projectName") String projectName) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
//        check(GschedulerProjectPurview.add, jwtUserInfo);
        return projectApi.importProcessDefinition(TenantCodeService.getSessionId(tenantCode), file, projectName, jwtUserInfo.getUserName()).apiResult();
    }

    @Autowired
    private GschedulerProjectPurviewService gschedulerProjectPurviewService;

    /**
     * 配置权限
     */
    @PostMapping("purview/{id}")
    public ApiResult purview(@PathVariable Integer id, @RequestBody List<UserGroupAndRoleVO> userGroupAndRoleVOS,
                             @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                             @RequestHeader(HttpHeadersParam.TOKEN) String token) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        check(id, GschedulerProjectPurview.configurePermissions, token, tenantCode);
        userGroupAndRoleVOS.forEach(s -> {
            GschedulerProjectPurview gschedulerProjectPurview = new GschedulerProjectPurview();
            gschedulerProjectPurview.setProjectId(id);
            gschedulerProjectPurview.setRoleId(s.getRoleId());
            s.getUserGroupId().forEach(sb -> {
                gschedulerProjectPurview.setUserGroupId(sb);
                gschedulerProjectPurviewService.save(gschedulerProjectPurview);
            });
        });
        return ApiResult.success();
    }

    /**
     * 通id获取当前用户的权限
     */
    @GetMapping("purview/get/{id}")
    public ApiResult purviewGetById(@PathVariable Integer id,
                                    @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                    @RequestHeader(HttpHeadersParam.TOKEN) String token) {
        Collection<Resource> resourcesByProjectId = gschedulerProjectPurviewService.getResourcesByProjectId(id, token, tenantCode);
        return ApiResult.success(resourcesByProjectId);
    }

    //是否开启业务权限配置
    @Value("${gscheduler.purview.flag:true}")
    private boolean purviewFlag = true;

    private void check(Integer id, @NonNull String resourcesParams, String token, String tenantCode) {
        if (purviewFlag) {
            Collection<Resource> resourcesByProjectId = gschedulerProjectPurviewService.getResourcesByProjectId(id, token, tenantCode);
            if (!resourcesByProjectId.contains(resourcesParams)) {
                log.error("参数：projectId={},resourcesParams={},tenantCode={},当前用户权限={}", id, resourcesParams, tenantCode, resourcesByProjectId);
                throw new RuntimeException("当前用户无权限操作");
            }
        }
    }
}

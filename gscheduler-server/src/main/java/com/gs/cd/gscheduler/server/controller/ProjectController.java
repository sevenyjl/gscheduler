package com.gs.cd.gscheduler.server.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    @Autowired
    private GschedulerProjectPurviewService gschedulerProjectPurviewService;


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
        gschedulerProjectPurviewService.check((Integer) null, GschedulerProjectPurview.add, token, tenantCode);
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
        gschedulerProjectPurviewService.check(projectId, GschedulerProjectPurview.edit, token, tenantCode);
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
        gschedulerProjectPurviewService.check(projectId, GschedulerProjectPurview.view, token, tenantCode);
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
        // TODO: 2021/5/21 这里改为admin的项目，来模拟删除，并不是真实删库，删库有风险
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        gschedulerProjectPurviewService.check(projectId, GschedulerProjectPurview.delete, token, tenantCode);
        //删除配置的权限
        boolean b = gschedulerProjectPurviewService.removeByProjectId(projectId);
        return projectApi.changeUserId(TenantCodeService.getSessionId(tenantCode), 1, projectId).apiResult();
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


    /**
     * 配置权限
     */
    @PostMapping("purview/{id}")
    public ApiResult purview(@PathVariable Integer id, @RequestBody List<UserGroupAndRoleVO> userGroupAndRoleVOS,
                             @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                             @RequestHeader(HttpHeadersParam.TOKEN) String token) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        for (UserGroupAndRoleVO userGroupAndRoleVO : userGroupAndRoleVOS) {
            if (StrUtil.isEmpty(userGroupAndRoleVO.getRoleId())) return ApiResult.error("角色信息不能为空");
            if (CollectionUtil.isEmpty(userGroupAndRoleVO.getUserGroupId())) return ApiResult.error("用户组信息不能为空");
        }
        gschedulerProjectPurviewService.check(id, GschedulerProjectPurview.configurePermissions, token, tenantCode);
        //删除原来的
        boolean remove = gschedulerProjectPurviewService.remove(new QueryWrapper<GschedulerProjectPurview>().lambda()
                .eq(GschedulerProjectPurview::getProjectId, id));
        userGroupAndRoleVOS.forEach(s -> {
            GschedulerProjectPurview gschedulerProjectPurview = new GschedulerProjectPurview();
            Result result = projectApi.queryProjectById(TenantCodeService.getSessionId(tenantCode), id);
            if (result.isSuccess()) {
                Project project = JSONUtil.parseObj(result.getData()).toBean(Project.class);
                gschedulerProjectPurview.setProjectName(project.getName());
                gschedulerProjectPurview.setProjectId(id);
                gschedulerProjectPurview.setRoleId(s.getRoleId());
                s.getUserGroupId().forEach(sb -> {
                    gschedulerProjectPurview.setUserGroupId(sb);
                    gschedulerProjectPurviewService.save(gschedulerProjectPurview);
                });
            } else {
                log.error("配置权限错误：{}", result);
                throw new RuntimeException(result.getMsg());
            }
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
//        Collection<Resource> resourcesByProjectId = gschedulerProjectPurviewService.getResourcesByProjectId(id, token, tenantCode);
        List<GschedulerProjectPurview> gschedulerProjectPurviews = gschedulerProjectPurviewService.listByProjectId(id);
        HashMap<String, UserGroupAndRoleVO> result = new HashMap<>();
        gschedulerProjectPurviews.forEach(s -> {
            UserGroupAndRoleVO orDefault = result.getOrDefault(s.getRoleId(), new UserGroupAndRoleVO());
            orDefault.setRoleId(s.getRoleId());
            List<String> userGroupId = orDefault.getUserGroupId();
            if (userGroupId == null) {
                ArrayList<String> strings = new ArrayList<>();
                strings.add(s.getUserGroupId());
                orDefault.setUserGroupId(strings);
            } else {
                userGroupId.add(s.getUserGroupId());
            }
            result.put(s.getRoleId(), orDefault);
        });
        return ApiResult.success(result.values());
    }

}

package com.gs.cd.gscheduler.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.service.ProjectService;
import com.gs.cd.gscheduler.common.entity.Project;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


/**
 * 项目管理接口
 *
 * @Author seven
 * @Date 2021/4/23 15:08
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("/gscheduler/projects")
@Log4j2
public class ProjectController {

    @Autowired
    ProjectService projectService;


    /**
     * 创建项目
     *
     * @param tenantCode  租户code
     * @param token       用户token
     * @param projectName 项目名称
     * @param description 项目描述
     * @return
     */
    @PostMapping(value = "/create")
    public ApiResult createProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                   @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                   @RequestParam("projectName") String projectName,
                                   @RequestParam(value = "description", required = false) String description) {
        log.info("create project name: {}, desc: {}", projectName, description);
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        return projectService.createProject(jwtUserInfo, projectName, description) ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 通过id更新项目
     *
     * @param tenantCode  租户code
     * @param token       用户token
     * @param projectId   项目id
     * @param projectName 项目名称
     * @param description 项目描述
     * @return
     */
    @PostMapping(value = "/update")
    public ApiResult updateProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                   @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                   @RequestParam("projectId") Integer projectId,
                                   @RequestParam("projectName") String projectName,
                                   @RequestParam(value = "description", required = false) String description) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {} , updateProcessInstance project name: {}, desc: {}", jwtUserInfo.getUserName(), projectName, description);

        return projectService.updateProject(jwtUserInfo, projectId, projectName, description) ? ApiResult.success() : ApiResult.error();
    }


    /**
     * 通过id查询项目
     *
     * @param tenantCode
     * @param token
     * @param projectId
     * @return
     */
    @GetMapping(value = "/query-by-id")
    public ApiResult queryProjectById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                      @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                      @RequestParam("projectId") Integer projectId) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, query project by id: {}", jwtUserInfo.getUserName(), projectId);
        return ApiResult.success(projectService.getById(projectId));
    }

    /**
     * 分页模糊查询
     * todo 工作流定义数 正在运行的流程数
     *
     * @param tenantCode
     * @param token
     * @param searchVal  模糊项目名称
     * @param pageSize
     * @param pageNo
     * @return
     */
    @GetMapping(value = "/list-paging")
    public ApiResult queryProjectListPaging(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                            @RequestParam(value = "searchVal", required = false) String searchVal,
                                            @RequestParam("pageSize") Integer pageSize,
                                            @RequestParam("pageNo") Integer pageNo
    ) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, query project list paging", jwtUserInfo.getUserName());
        QueryWrapper<Project> tDsProjectQueryWrapper = new QueryWrapper<>();
        if (searchVal != null) {
            tDsProjectQueryWrapper.lambda().like(Project::getName, "%" + searchVal + "%");
        }
        IPage<Project> page = projectService.page(new Page<>(pageNo, pageSize), tDsProjectQueryWrapper);
        return ApiResult.success(page);
    }

    /**
     * 通过id删除项目
     *
     * @param tenantCode
     * @param token
     * @param projectId
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                   @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                   @RequestParam("projectId") Integer projectId
    ) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, delete project: {}.", jwtUserInfo.getUserName(), projectId);
        return projectService.removeById(projectId) ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 没有授权的项目
     *
     * @param tenantCode
     * @param token
     * @param userId
     * @return
     */
    @GetMapping(value = "/unauth-project")
    public ApiResult queryUnauthorizedProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @RequestParam("userId") Integer userId) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, query unauthorized project by user id: {}.", jwtUserInfo.getUserName(), userId);
        // TODO: 2021/4/23 查询当前用户不被授权的project
        return ApiResult.error("未完成");
    }

    /**
     * 授权的项目
     *
     * @param tenantCode
     * @param token
     * @param userId
     * @return
     */
    @GetMapping(value = "/authed-project")
    public ApiResult queryAuthorizedProject(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                            @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                            @RequestParam("userId") Integer userId) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, query authorized project by user id: {}.", jwtUserInfo.getUserName(), userId);
        // TODO: 2021/4/23 查询当前用户被授权的project
        return ApiResult.error("未完成");
    }

    /**
     * 导入工作流
     *
     * @param tenantCode
     * @param token
     * @param file
     * @param projectName
     * @return
     */
    @PostMapping(value = "/import-definition")
    public ApiResult importProcessDefinition(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                             @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                             @RequestParam("file") MultipartFile file,
                                             @RequestParam("projectName") String projectName) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("import process definition by id, login user:{}, project: {}",
                jwtUserInfo.getUserName(), projectName);
        // TODO: 2021/4/23 导入工作流定义
        return ApiResult.error("未完成");
    }

    /**
     * 获取所有项目列表
     *
     * @param tenantCode
     * @param token
     * @return
     */
    @GetMapping(value = "/query-project-list")
    public ApiResult queryAllProjectList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                         @RequestHeader(HttpHeadersParam.TOKEN) String token) {
        JwtUserInfo jwtUserInfo = JwtUtils.getJwtUserInfo(token);
        log.info("login user {}, query all project list", jwtUserInfo.getUserName());
        List<Project> list = projectService.list();
        return ApiResult.success(list);
    }


    // TODO: 2021/4/23 配置权限接口

}

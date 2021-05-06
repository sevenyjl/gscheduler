package com.gs.cd.gscheduler.api.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;
import com.gs.cd.gscheduler.api.service.ProcessInstanceService;
import com.gs.cd.gscheduler.api.utils.PageInfo;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.ProcessInstance;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.enums.Flag;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流实例管理
 *
 * @Author seven
 * @Date 2021/4/29 14:45
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("/gscheduler/projects/{projectName}/instance")
@Log4j2
public class ProcessInstanceController {

    @Autowired
    ProcessInstanceService processInstanceService;

    /**
     * 分页查询
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processDefinitionId
     * @param searchVal
     * @param executorName
     * @param stateType
     * @param host
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list-paging")
    public ApiResult queryProcessInstanceList(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @PathVariable String projectName,
                                              @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                                              @RequestParam(value = "searchVal", required = false) String searchVal,
                                              @RequestParam(value = "executorName", required = false) String executorName,
                                              @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                              @RequestParam(value = "host", required = false) String host,
                                              @RequestParam(value = "startDate", required = false) String startTime,
                                              @RequestParam(value = "endDate", required = false) String endTime,
                                              @RequestParam("pageNo") Integer pageNo,
                                              @RequestParam("pageSize") Integer pageSize) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query all process instance list, login user:{},project name:{}, define id:{}," +
                        "search value:{},executor name:{},state type:{},host:{},start time:{}, end time:{},page number:{}, page size:{}",
                loginUser.getUserName(), projectName, processDefinitionId, searchVal, executorName, stateType, host,
                startTime, endTime, pageNo, pageSize);
        QueryWrapper<ProcessInstance> processInstanceQueryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(searchVal)) {
            processInstanceQueryWrapper.lambda().like(ProcessInstance::getName, "%" + searchVal + "%");
        }
        if (StrUtil.isNotEmpty(executorName)) {
            processInstanceQueryWrapper.lambda().eq(ProcessInstance::getExecutorName, executorName);
        }
        if (stateType != null) {
            processInstanceQueryWrapper.lambda().eq(ProcessInstance::getState, stateType);
        }
        if (StrUtil.isNotEmpty(host)) {
            processInstanceQueryWrapper.lambda().eq(ProcessInstance::getHost, host);
        }
        if (StrUtil.isNotEmpty(startTime)) {
            DateTime startime = DateUtil.parse(startTime, Constants.YYYY_MM_DD_HH_MM_SS);
            processInstanceQueryWrapper.lambda().ge(ProcessInstance::getStartTime, startime);
        }
        if (StrUtil.isNotEmpty(endTime)) {
            DateTime endtime = DateUtil.parse(endTime, Constants.YYYY_MM_DD_HH_MM_SS);
            processInstanceQueryWrapper.lambda().le(ProcessInstance::getStartTime, endtime);
        }
        IPage<ProcessInstance> page = processInstanceService.page(new Page<>(pageNo, pageSize), processInstanceQueryWrapper);
        return ApiResult.success(PageInfo.pageInfoTrans(page));
    }

    /**
     * 通过实例id获取
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceId
     * @return
     * @throws IOException
     */
    @GetMapping(value = "/task-list-by-process-id")
    public ApiResult queryTaskListByProcessId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @PathVariable String projectName,
                                              @RequestParam("processInstanceId") Integer processInstanceId
    ) throws IOException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query task instance list by process instance id, login user:{}, project name:{}, process instance id:{}",
                loginUser.getUserName(), projectName, processInstanceId);
        Map<String, Object> result = processInstanceService.queryTaskListByProcessId(projectName, processInstanceId);
        return ApiResult.success(result);
    }

    /**
     * 更新
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceJson
     * @param processInstanceId
     * @param scheduleTime
     * @param syncDefine
     * @param locations
     * @param connects
     * @param flag
     * @return
     * @throws ParseException
     */
    @PostMapping(value = "/update")
    public ApiResult updateProcessInstance(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                           @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                           @PathVariable String projectName,
                                           @RequestParam(value = "processInstanceJson", required = false) String processInstanceJson,
                                           @RequestParam(value = "processInstanceId") Integer processInstanceId,
                                           @RequestParam(value = "scheduleTime", required = false) String scheduleTime,
                                           @RequestParam(value = "syncDefine", required = true) Boolean syncDefine,
                                           @RequestParam(value = "locations", required = false) String locations,
                                           @RequestParam(value = "connects", required = false) String connects,
                                           @RequestParam(value = "flag", required = false) Flag flag
    ) throws ParseException {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("updateProcessInstance process instance, login user:{}, project name:{}, process instance json:{}," +
                        "process instance id:{}, schedule time:{}, sync define:{}, flag:{}, locations:{}, connects:{}",
                loginUser.getUserName(), projectName, processInstanceJson, processInstanceId, scheduleTime,
                syncDefine, flag, locations, connects);
        boolean b = processInstanceService.updateProcessInstance(loginUser, projectName,
                processInstanceId, processInstanceJson, scheduleTime, syncDefine, flag, locations, connects);
        return b ? ApiResult.success() : ApiResult.error();
    }

    /**
     * 通过id选择
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/select-by-id")
    public ApiResult queryProcessInstanceById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                              @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                              @PathVariable String projectName,
                                              @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("query process instance detail by id, login user:{},project name:{}, process instance id:{}",
                loginUser.getUserName(), projectName, processInstanceId);
        ProcessInstance processInstance = processInstanceService.getById(processInstanceId);
        return ApiResult.success(processInstance);
    }


    /**
     * 删除
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceId
     * @return
     */
    @GetMapping(value = "/delete")
    public ApiResult deleteProcessInstanceById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                               @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                               @PathVariable String projectName,
                                               @RequestParam("processInstanceId") Integer processInstanceId
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("delete process instance by id, login user:{}, project name:{}, process instance id:{}",
                loginUser.getUserName(), projectName, processInstanceId);
        // task queue
        boolean b = processInstanceService.removeById(processInstanceId);
        return b ? ApiResult.success() : ApiResult.error();
    }

    /**
     * select-sub-process
     * @param tenantCode
     * @param token
     * @param projectName
     * @param taskId
     * @return
     */
    @GetMapping(value = "/select-sub-process")
    public ApiResult querySubProcessInstanceByTaskId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                     @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                     @PathVariable String projectName,
                                                     @RequestParam("taskId") Integer taskId) {
        return ApiResult.error("未开发");
    }

    /**
     * select-parent-process
     * @param tenantCode
     * @param token
     * @param projectName
     * @param subId
     * @return
     */
    @GetMapping(value = "/select-parent-process")
    public ApiResult queryParentInstanceBySubId(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                @PathVariable String projectName,
                                                @RequestParam("subId") Integer subId) {
        return ApiResult.error("未开发");
    }


    /**
     * view-variables
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceId
     * @return
     * @throws Exception
     */
    @GetMapping(value = "/view-variables")
    public ApiResult viewVariables(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                   @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                   @PathVariable String projectName,
                                   @RequestParam("processInstanceId") Integer processInstanceId) throws Exception {
        return ApiResult.error("未开发");
    }

    /**
     * 批量删除
     * @param tenantCode
     * @param token
     * @param projectName
     * @param processInstanceIds 9,1,3,2
     * @return
     */
    @GetMapping(value = "/batch-delete")
    public ApiResult batchDeleteProcessInstanceByIds(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                                     @RequestHeader(HttpHeadersParam.TOKEN) String token,
                                                     @PathVariable String projectName,
                                                     @RequestParam("processInstanceIds") String processInstanceIds
    ) {
        JwtUserInfo loginUser = JwtUtils.getJwtUserInfo(token);
        log.info("delete process instance by ids, login user:{}, project name:{}, process instance ids :{}",
                loginUser.getUserName(), projectName, processInstanceIds);
        if (StrUtil.isNotEmpty(processInstanceIds)) {
            String[] processInstanceIdArray = processInstanceIds.split(",");
            for (String strProcessInstanceId : processInstanceIdArray) {
                int processInstanceId = Integer.parseInt(strProcessInstanceId);
                processInstanceService.removeById(processInstanceId);
            }
        }
        return ApiResult.success();
    }


}

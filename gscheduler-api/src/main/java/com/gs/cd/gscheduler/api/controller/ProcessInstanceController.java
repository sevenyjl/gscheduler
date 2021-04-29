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
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.ProcessInstance;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

/**
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
        return ApiResult.success(page);
    }


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
}

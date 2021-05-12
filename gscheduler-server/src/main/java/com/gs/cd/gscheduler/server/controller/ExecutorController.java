package com.gs.cd.gscheduler.server.controller;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.api.ExecutorApi;
import com.gs.cd.gscheduler.enums.ExecuteType;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.apache.dolphinscheduler.common.enums.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * execute process controller
 */
@RestController
@RequestMapping("/projects/{projectName}/executors")
public class ExecutorController {


    @Autowired
    private ExecutorApi executorApi;

    /**
     * 开始运行工作流
     *
     * @param tenantCode              租户code
     * @param projectName             项目名称
     * @param processDefinitionId     工作流定义id
     * @param scheduleTime
     * @param failureStrategy         失败策略
     * @param startNodeList
     * @param taskDependType
     * @param execType
     * @param warningType             告警类型
     * @param warningGroupId          告警组id
     * @param receivers
     * @param receiversCc
     * @param runMode
     * @param processInstancePriority
     * @param workerGroup             工作组
     * @param timeout
     * @return
     */
    @PostMapping(value = "start-process-instance")
    public ApiResult startProcessInstance(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam(value = "processDefinitionId") int processDefinitionId,
            @RequestParam(value = "scheduleTime", required = false) String scheduleTime,
            @RequestParam(value = "failureStrategy", required = true) FailureStrategy failureStrategy,
            @RequestParam(value = "startNodeList", required = false) String startNodeList,
            @RequestParam(value = "taskDependType", required = false) TaskDependType taskDependType,
            @RequestParam(value = "execType", required = false) CommandType execType,
            @RequestParam(value = "warningType", required = true) WarningType warningType,
            @RequestParam(value = "warningGroupId", required = false) int warningGroupId,
            @RequestParam(value = "receivers", required = false) String receivers,
            @RequestParam(value = "receiversCc", required = false) String receiversCc,
            @RequestParam(value = "runMode", required = false) RunMode runMode,
            @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority,
            @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
            @RequestParam(value = "timeout", required = false) Integer timeout) {
        return executorApi.startProcessInstance(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId, scheduleTime, failureStrategy, startNodeList,
                taskDependType, execType, warningType, warningGroupId, receivers,
                receiversCc, runMode, processInstancePriority, workerGroup, timeout
        ).apiResult();
    }


    /**
     * @param tenantCode        租户code
     * @param projectName       项目名称
     * @param processInstanceId
     * @param executeType
     * @return
     */
    @PostMapping(value = "/execute")
    public ApiResult execute(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String projectName,
            @RequestParam("processInstanceId") Integer processInstanceId,
            @RequestParam("executeType") ExecuteType executeType
    ) {
        return executorApi.execute(TenantCodeService.getSessionId(tenantCode),
                projectName, processInstanceId, executeType).apiResult();
    }

    @PostMapping(value = "/start-check")
    public ApiResult startCheckProcessDefinition(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam(value = "processDefinitionId") int processDefinitionId) {
        return executorApi.startCheckProcessDefinition(TenantCodeService.getSessionId(tenantCode), processDefinitionId).apiResult();
    }


    @GetMapping(value = "/get-receiver-cc")
    public ApiResult getReceiverCc(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @RequestParam(value = "processDefinitionId", required = false) Integer processDefinitionId,
            @RequestParam(value = "processInstanceId", required = false) Integer processInstanceId) {
        return executorApi.getReceiverCc(TenantCodeService.getSessionId(tenantCode), processDefinitionId, processInstanceId).apiResult();
    }


}
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
@RequestMapping("projects/{projectName}/executors")
public class ExecutorController {


    @Autowired
    private ExecutorApi executorApi;

    /**
     * 开始运行工作流
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
     * @param processDefinitionId 工作流定义id
     * @param scheduleTime
     * @param failureStrategy 失败策略
     * @param startNodeList
     * @param taskDependType
     * @param execType
     * @param warningType 告警类型
     * @param warningGroupId 告警组id
     * @param receivers
     * @param receiversCc
     * @param runMode
     * @param processInstancePriority
     * @param workerGroup 工作组
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
     *
     * @param tenantCode  租户code
     * @param projectName 项目名称
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
        return executorApi.startProcessInstance(TenantCodeService.getSessionId(tenantCode),
                projectName, processDefinitionId, scheduleTime, failureStrategy, startNodeList,
                taskDependType, execType, warningType, warningGroupId, receivers,
                receiversCc, runMode, processInstancePriority, workerGroup, timeout
        ).apiResult();
    }

    /**
     * check process definition and all of the son process definitions is on line.
     *
     * @param loginUser           login user
     * @param processDefinitionId process definition id
     * @return check ApiResult code
     */
    @ApiOperation(value = "startCheckProcessDefinition", notes = "START_CHECK_PROCESS_DEFINITION_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", required = true, dataType = "Int", example = "100")
    })
    @PostMapping(value = "/start-check")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(CHECK_PROCESS_DEFINITION_ERROR)
    public ApiResult startCheckProcessDefinition(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                                 @RequestParam(value = "processDefinitionId") int processDefinitionId) {
        logger.info("login user {}, check process definition {}", loginUser.getUserName(), processDefinitionId);
        Map<String, Object> ApiResult = execService.startCheckByProcessDefinedId(processDefinitionId);
        return returnDataList(ApiResult);
    }

    /**
     * query recipients and copyers by process definition ID
     *
     * @param loginUser           login user
     * @param processDefinitionId process definition id
     * @param processInstanceId   process instance id
     * @return receivers cc list
     */
    @ApiIgnore
    @ApiOperation(value = "getReceiverCc", notes = "GET_RECEIVER_CC_NOTES")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "processDefinitionId", value = "PROCESS_DEFINITION_ID", required = true, dataType = "Int", example = "100"),
            @ApiImplicitParam(name = "processInstanceId", value = "PROCESS_INSTANCE_ID", required = true, dataType = "Int", example = "100")

    })
    @GetMapping(value = "/get-receiver-cc")
    @ResponseStatus(HttpStatus.OK)
    @ApiException(QUERY_RECIPIENTS_AND_COPYERS_BY_PROCESS_DEFINITION_ERROR)
    public ApiResult getReceiverCc(@ApiIgnore @RequestAttribute(value = Constants.SESSION_USER) User loginUser,
                                   @RequestParam(value = "processDefinitionId", required = false) Integer processDefinitionId,
                                   @RequestParam(value = "processInstanceId", required = false) Integer processInstanceId) {
        logger.info("login user {}, get process definition receiver and cc", loginUser.getUserName());
        Map<String, Object> ApiResult = execService.getReceiverCc(processDefinitionId, processInstanceId);
        return returnDataList(ApiResult);
    }


}
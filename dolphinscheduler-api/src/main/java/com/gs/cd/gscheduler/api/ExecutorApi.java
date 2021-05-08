package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.enums.ExecuteType;
import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.enums.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/4/13 16:15
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "ExecutorApi")

public interface ExecutorApi {
    @PostMapping(value = "/projects/{projectName}/executors/start-process-instance")
    public Result startProcessInstance(@RequestHeader(name = "Cookie") String sessinoId,
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
                                       @RequestParam(value = "timeout", required = false) Integer timeout);

    @PostMapping(value = "/projects/{projectName}/executors/execute")
    public Result execute(@RequestHeader(name = "Cookie") String sessinoId,
                          @PathVariable String projectName,
                          @RequestParam("processInstanceId") Integer processInstanceId,
                          @RequestParam("executeType") ExecuteType executeType
    );

    @PostMapping(value = "/projects/{projectName}/executors/start-check")
    public Result startCheckProcessDefinition(@RequestHeader(name = "Cookie") String sessinoId,
                                              @RequestParam(value = "processDefinitionId") int processDefinitionId);

    @GetMapping(value = "/projects/{projectName}/executors/get-receiver-cc")
    public Result getReceiverCc(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam(value = "processDefinitionId", required = false) Integer processDefinitionId,
                                @RequestParam(value = "processInstanceId", required = false) Integer processInstanceId);
}

package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.ExecutionStatus;
import org.apache.dolphinscheduler.common.enums.Flag;
import org.apache.dolphinscheduler.common.utils.ParameterUtils;
import org.apache.dolphinscheduler.common.utils.StringUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author seven
 * @Date 2021/4/13 16:21
 * @Description
 * @Version 1.0
 */

@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "ProcessInstanceApi")
public interface ProcessInstanceApi {

    @GetMapping(value = "/projects/{projectName}/instance/list-paging")
    public Result queryProcessInstanceList(@RequestHeader(name = "Cookie") String sessinoId,
                                           @PathVariable String projectName,
                                           @RequestParam(value = "processDefinitionId", required = false, defaultValue = "0") Integer processDefinitionId,
                                           @RequestParam(value = "searchVal", required = false) String searchVal,
                                           @RequestParam(value = "executorName", required = false) String executorName,
                                           @RequestParam(value = "stateType", required = false) ExecutionStatus stateType,
                                           @RequestParam(value = "host", required = false) String host,
                                           @RequestParam(value = "startDate", required = false) String startTime,
                                           @RequestParam(value = "endDate", required = false) String endTime,
                                           @RequestParam("pageNo") Integer pageNo,
                                           @RequestParam("pageSize") Integer pageSize);

    @GetMapping(value = "/projects/{projectName}/instance/task-list-by-process-id")
    public Result queryTaskListByProcessId(@RequestHeader(name = "Cookie") String sessinoId,
                                           @PathVariable String projectName,
                                           @RequestParam("processInstanceId") Integer processInstanceId
    );


    @PostMapping(value = "/projects/{projectName}/instance/update")
    public Result updateProcessInstance(@RequestHeader(name = "Cookie") String sessinoId,
                                        @PathVariable String projectName,
                                        @RequestParam(value = "processInstanceJson", required = false) String processInstanceJson,
                                        @RequestParam(value = "processInstanceId") Integer processInstanceId,
                                        @RequestParam(value = "scheduleTime", required = false) String scheduleTime,
                                        @RequestParam(value = "syncDefine", required = true) Boolean syncDefine,
                                        @RequestParam(value = "locations", required = false) String locations,
                                        @RequestParam(value = "connects", required = false) String connects,
                                        @RequestParam(value = "flag", required = false) Flag flag
    );

    @GetMapping(value = "/projects/{projectName}/instance/select-by-id")
    public Result queryProcessInstanceById(@RequestHeader(name = "Cookie") String sessinoId,
                                           @PathVariable String projectName,
                                           @RequestParam("processInstanceId") Integer processInstanceId
    );

    @GetMapping(value = "/projects/{projectName}/instance/delete")
    public Result deleteProcessInstanceById(@RequestHeader(name = "Cookie") String sessinoId,
                                            @PathVariable String projectName,
                                            @RequestParam("processInstanceId") Integer processInstanceId
    );

    @GetMapping(value = "/projects/{projectName}/instance/select-sub-process")
    @ResponseStatus(HttpStatus.OK)
    public Result querySubProcessInstanceByTaskId(@RequestHeader(name = "Cookie") String sessinoId,
                                                  @PathVariable String projectName,
                                                  @RequestParam("taskId") Integer taskId);

    @GetMapping(value = "/projects/{projectName}/instance/select-parent-process")
    public Result queryParentInstanceBySubId(@RequestHeader(name = "Cookie") String sessinoId,
                                             @PathVariable String projectName,
                                             @RequestParam("subId") Integer subId);

    @GetMapping(value = "/projects/{projectName}/instance/view-variables")
    public Result viewVariables(@RequestHeader(name = "Cookie") String sessinoId,
                                @RequestParam("processInstanceId") Integer processInstanceId);

    @GetMapping(value = "/projects/{projectName}/instance/view-gantt")
    public Result viewTree(@RequestHeader(name = "Cookie") String sessinoId,
                           @PathVariable String projectName,
                           @RequestParam("processInstanceId") Integer processInstanceId);


    @GetMapping(value = "/projects/{projectName}/instance/batch-delete")
    public Result batchDeleteProcessInstanceByIds(@RequestHeader(name = "Cookie") String sessinoId,
                                                  @PathVariable String projectName,
                                                  @RequestParam("processInstanceIds") String processInstanceIds
    );
}

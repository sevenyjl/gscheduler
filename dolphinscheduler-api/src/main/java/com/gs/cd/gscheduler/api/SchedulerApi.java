package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.enums.FailureStrategy;
import org.apache.dolphinscheduler.common.enums.Priority;
import org.apache.dolphinscheduler.common.enums.WarningType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import static org.apache.dolphinscheduler.common.Constants.SESSION_USER;

/**
 * @Author seven
 * @Date 2021/4/13 16:49
 * @Description
 * @Version 1.0
 */

@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "SchedulerApi")
public interface SchedulerApi {
    public static final String DEFAULT_WARNING_TYPE = "NONE";
    public static final String DEFAULT_NOTIFY_GROUP_ID = "1";
    public static final String DEFAULT_FAILURE_POLICY = "CONTINUE";

    @PostMapping("/projects/{projectName}/schedule/create")
    public Result createSchedule(@RequestHeader(name = "Cookie") String sessinoId,
                                 @PathVariable String projectName,
                                 @RequestParam(value = "processDefinitionId") Integer processDefinitionId,
                                 @RequestParam(value = "schedule") String schedule,
                                 @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                 @RequestParam(value = "warningGroupId", required = false, defaultValue = DEFAULT_NOTIFY_GROUP_ID) int warningGroupId,
                                 @RequestParam(value = "failureStrategy", required = false, defaultValue = DEFAULT_FAILURE_POLICY) FailureStrategy failureStrategy,
                                 @RequestParam(value = "receivers", required = false) String receivers,
                                 @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                 @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                 @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority);


    @PostMapping("/projects/{projectName}/schedule/update")
    public Result updateSchedule(@RequestHeader(name = "Cookie") String sessinoId,
                                 @PathVariable String projectName,
                                 @RequestParam(value = "id") Integer id,
                                 @RequestParam(value = "schedule") String schedule,
                                 @RequestParam(value = "warningType", required = false, defaultValue = DEFAULT_WARNING_TYPE) WarningType warningType,
                                 @RequestParam(value = "warningGroupId", required = false) int warningGroupId,
                                 @RequestParam(value = "failureStrategy", required = false, defaultValue = "END") FailureStrategy failureStrategy,
                                 @RequestParam(value = "receivers", required = false) String receivers,
                                 @RequestParam(value = "receiversCc", required = false) String receiversCc,
                                 @RequestParam(value = "workerGroup", required = false, defaultValue = "default") String workerGroup,
                                 @RequestParam(value = "processInstancePriority", required = false) Priority processInstancePriority);

    @PostMapping("/projects/{projectName}/schedule/online")
    public Result online(@RequestHeader(name = "Cookie") String sessinoId,
                         @PathVariable("projectName") String projectName,
                         @RequestParam("id") Integer id);

    @PostMapping("/projects/{projectName}/schedule/offline")
    public Result offline(@RequestHeader(name = "Cookie") String sessinoId,
                          @PathVariable("projectName") String projectName,
                          @RequestParam("id") Integer id);


    @GetMapping("/projects/{projectName}/schedule/list-paging")
    public Result queryScheduleListPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                          @PathVariable String projectName,
                                          @RequestParam Integer processDefinitionId,
                                          @RequestParam(value = "searchVal", required = false) String searchVal,
                                          @RequestParam("pageNo") Integer pageNo,
                                          @RequestParam("pageSize") Integer pageSize);

    @GetMapping(value = "/projects/{projectName}/schedule/delete")
    public Result deleteScheduleById(@RequestHeader(name = "Cookie") String sessinoId,
                                     @PathVariable String projectName,
                                     @RequestParam("scheduleId") Integer scheduleId
    );

    @PostMapping("/projects/{projectName}/schedule/list")
    public Result queryScheduleList(@RequestHeader(name = "Cookie") String sessinoId,
                                    @PathVariable String projectName);

    @PostMapping("/projects/{projectName}/schedule/preview")
    public Result previewSchedule(@RequestHeader(name = "Cookie") String sessinoId,
                                  @PathVariable String projectName,
                                  @RequestParam(value = "schedule") String schedule
    );
}

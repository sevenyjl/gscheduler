package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/4/13 16:04
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}/projects/analysis", name = "DataAnalysisApi")
public interface DataAnalysisApi {
    @GetMapping(value = "/task-state-count")
    public Result countTaskState(@RequestHeader(name = "Cookie") String sessinoId,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId);

    @GetMapping(value = "/process-state-count")
    public Result countProcessInstanceState(@RequestHeader(name = "Cookie") String sessinoId,
                                            @RequestParam(value = "startDate", required = false) String startDate,
                                            @RequestParam(value = "endDate", required = false) String endDate,
                                            @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId);

    @GetMapping(value = "/define-user-count")
    public Result countDefinitionByUser(@RequestHeader(name = "Cookie") String sessinoId,
                                        @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId);


    @GetMapping(value = "/command-state-count")
    public Result countCommandState(@RequestHeader(name = "Cookie") String sessinoId,
                                    @RequestParam(value = "startDate", required = false) String startDate,
                                    @RequestParam(value = "endDate", required = false) String endDate,
                                    @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId);


    @GetMapping(value = "/queue-count")
    public Result countQueueState(@RequestHeader(name = "Cookie") String sessinoId,
                                  @RequestParam(value = "projectId", required = false, defaultValue = "0") int projectId);
}

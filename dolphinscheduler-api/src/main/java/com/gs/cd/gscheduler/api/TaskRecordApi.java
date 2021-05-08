package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/4/13 16:57
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}/projects/task-record", name = "TaskRecordApi")
public interface TaskRecordApi {
    @GetMapping("/list-paging")
    public Result queryTaskRecordListPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                            @RequestParam(value = "taskName", required = false) String taskName,
                                            @RequestParam(value = "state", required = false) String state,
                                            @RequestParam(value = "sourceTable", required = false) String sourceTable,
                                            @RequestParam(value = "destTable", required = false) String destTable,
                                            @RequestParam(value = "taskDate", required = false) String taskDate,
                                            @RequestParam(value = "startDate", required = false) String startTime,
                                            @RequestParam(value = "endDate", required = false) String endTime,
                                            @RequestParam("pageNo") Integer pageNo,
                                            @RequestParam("pageSize") Integer pageSize
    );

    @GetMapping("/history-list-paging")
    public Result queryHistoryTaskRecordListPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                                   @RequestParam(value = "taskName", required = false) String taskName,
                                                   @RequestParam(value = "state", required = false) String state,
                                                   @RequestParam(value = "sourceTable", required = false) String sourceTable,
                                                   @RequestParam(value = "destTable", required = false) String destTable,
                                                   @RequestParam(value = "taskDate", required = false) String taskDate,
                                                   @RequestParam(value = "startDate", required = false) String startTime,
                                                   @RequestParam(value = "endDate", required = false) String endTime,
                                                   @RequestParam("pageNo") Integer pageNo,
                                                   @RequestParam("pageSize") Integer pageSize
    );


}

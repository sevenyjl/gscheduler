package com.gs.cd.gscheduler.api;


import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}/queue", name = "QueueApi")
public interface QueueApi {

    @GetMapping(value = "/list")
    public Result queryList(@RequestHeader(name = "Cookie") String sessinoId);

    @GetMapping(value = "/list-paging")
    public Result queryQueueListPaging(@RequestHeader(name = "Cookie") String sessinoId,
                                       @RequestParam("pageNo") Integer pageNo,
                                       @RequestParam(value = "searchVal", required = false) String searchVal,
                                       @RequestParam("pageSize") Integer pageSize);


    @PostMapping(value = "/create")
    public Result createQueue(@RequestHeader(name = "Cookie") String sessinoId,
                              @RequestParam(value = "queue") String queue,
                              @RequestParam(value = "queueName") String queueName);


    @PostMapping(value = "/update")
    public Result updateQueue(@RequestHeader(name = "Cookie") String sessinoId,
                              @RequestParam(value = "id") int id,
                              @RequestParam(value = "queue") String queue,
                              @RequestParam(value = "queueName") String queueName);


    @PostMapping(value = "/verify-queue")
    public Result verifyQueue(@RequestHeader(name = "Cookie") String sessinoId,
                              @RequestParam(value = "queue") String queue,
                              @RequestParam(value = "queueName") String queueName
    );
}

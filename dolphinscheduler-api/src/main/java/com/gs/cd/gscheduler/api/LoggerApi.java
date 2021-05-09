package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.apache.dolphinscheduler.common.Constants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "LoggerApi")
public interface LoggerApi {

    @GetMapping(value = "/log/detail")
    public Result queryLog(@RequestHeader(name = "Cookie") String sessinoId,
                           @RequestParam(value = "taskInstanceId") int taskInstanceId,
                           @RequestParam(value = "skipLineNum") int skipNum,
                           @RequestParam(value = "limit") int limit);


    @GetMapping(value = "/log/download-log")
    public ResponseEntity downloadTaskLog(@RequestHeader(name = "Cookie") String sessinoId,
                                          @RequestParam(value = "taskInstanceId") int taskInstanceId);
}

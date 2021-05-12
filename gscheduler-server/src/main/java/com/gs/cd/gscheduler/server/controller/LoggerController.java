package com.gs.cd.gscheduler.server.controller;


import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.cloud.utils.jwt.JwtUtils;

import com.gs.cd.cloud.common.ApiResult;


import com.gs.cd.gscheduler.api.LoggerApi;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * log controller
 */

@RestController
@RequestMapping("/log")
public class LoggerController {

    @Autowired
    private LoggerApi loggerApi;

    /**
     * query task log
     *
     * @param taskInstanceId task instance id
     * @param skipNum        skip number
     * @param limit          limit
     * @return task log content
     */


    @GetMapping(value = "/detail")
    public ApiResult queryLog(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                              @RequestParam(value = "taskInstanceId") int taskInstanceId,
                              @RequestParam(value = "skipLineNum") int skipNum,
                              @RequestParam(value = "limit") int limit) {
        return loggerApi.queryLog(TenantCodeService.getSessionId(tenantCode), taskInstanceId, skipNum, limit).apiResult();
    }


    /**
     * download log file
     *
     * @param taskInstanceId task instance id
     * @return log file content
     */


    @GetMapping(value = "/download-log")
    public ResponseEntity downloadTaskLog(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                          @RequestParam(value = "taskInstanceId") int taskInstanceId) {
        return loggerApi.downloadTaskLog(TenantCodeService.getSessionId(tenantCode), taskInstanceId);
    }

}
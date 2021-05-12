package com.gs.cd.gscheduler.trigger.server.controller;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.trigger.server.service.GschedulerTriggerService;
import com.gs.cd.gscheduler.trigger.server.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.trigger.server.entity.HttpParams;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/5/10 16:43
 * @Description 触发器
 * @Version 1.0
 */
@RestController
@RequestMapping("/gtrigger")
public class TriggerController {

    @Autowired
    GschedulerTriggerService gschedulerTriggerService;

    @GetMapping(value = "health")
    ApiResult health() {
        return ApiResult.success("gscheduler-trigger-server <version>1.1.0-SNAPSHOT</version>");
    }

    @GetMapping(value = "")
    ApiResult getByTaskIdAndGroupName(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                      @RequestParam String taskId,
                                      @RequestParam String groupName) {
        GschedulerTrigger byTaskIdAndGroupName = gschedulerTriggerService.getByTaskIdAndGroupName(tenantCode, taskId, groupName);
        byTaskIdAndGroupName.params2ITrigger();
        return ApiResult.success(byTaskIdAndGroupName);
    }

    @PostMapping(value = "/create/{taskId}/{groupName}")
    public ApiResult create(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @PathVariable String taskId,
                            @PathVariable String groupName,
                            @RequestBody HttpParams httpParams) {
        GschedulerTrigger gschedulerTrigger = new GschedulerTrigger();
        gschedulerTrigger.setTaskId(taskId);
        gschedulerTrigger.setGroupName(groupName);
        gschedulerTrigger.setITrigger(httpParams);
        gschedulerTrigger.setTenantCode(tenantCode);
        gschedulerTrigger.iTrigger2Params();
        return gschedulerTriggerService.create(gschedulerTrigger) ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/delete")
    ApiResult delete(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                     @NonNull @RequestParam String taskId,
                     @NonNull @RequestParam String groupName) {
        return gschedulerTriggerService.delete(tenantCode, taskId, groupName) ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/update/{taskId}/{groupName}")
    ApiResult updateByTaskIdAndGroupName(
            @NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @NonNull @PathVariable String taskId,
            @NonNull @PathVariable String groupName,
            @NonNull @RequestBody HttpParams httpParams) {
        GschedulerTrigger gschedulerTrigger = new GschedulerTrigger();
        gschedulerTrigger.setTaskId(taskId);
        gschedulerTrigger.setGroupName(groupName);
        gschedulerTrigger.setITrigger(httpParams);
        gschedulerTrigger.setTenantCode(tenantCode);
        gschedulerTrigger.iTrigger2Params();
        return gschedulerTriggerService.edit(gschedulerTrigger) ? ApiResult.success() : ApiResult.error();
    }
}

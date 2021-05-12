package com.gs.cd.trigger.controller;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.trigger.entity.GSchedulerTriggerHttp;
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
@RequestMapping("trigger")
public class TriggerController {
//
//    @Autowired
//    TriggerService triggerService;
//
//    @GetMapping(value = "")
//    ApiResult getByTaskIdAndGroupName(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                                      @NonNull @RequestParam String taskId,
//                                      @NonNull @RequestParam String groupName) {
//        return ApiResult.success(triggerService.getByTaskIdAndGroupName(tenantCode, taskId, groupName));
//    }
//
//    @PostMapping(value = "/create/{taskId}/{groupName}")
//    public ApiResult create(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                            @NonNull @PathVariable String taskId,
//                            @NonNull @PathVariable String groupName,
//                            @NonNull @RequestBody GSchedulerTriggerHttp params) {
//        params.setTaskId(taskId);
//        params.setGroupName(groupName);
//        return triggerService.create(tenantCode, params) ? ApiResult.success() : ApiResult.error();
//    }
//
//    @PostMapping(value = "/delete")
//    ApiResult delete(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//                     @NonNull @RequestParam String taskId,
//                     @NonNull @RequestParam String groupName) {
//        return triggerService.delete(tenantCode, taskId, groupName) ? ApiResult.success() : ApiResult.error();
//    }
//
//    @PostMapping(value = "/update/{taskId}/{groupName}")
//    ApiResult updateByTaskIdAndGroupName(
//            @NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
//            @NonNull @PathVariable String taskId,
//            @NonNull @PathVariable String groupName,
//            @NonNull @RequestBody GSchedulerTriggerHttp params) {
//        params.setTaskId(taskId);
//        params.setGroupName(groupName);
//        return triggerService.update(tenantCode, taskId, groupName) ? ApiResult.success() : ApiResult.error();
//    }
}

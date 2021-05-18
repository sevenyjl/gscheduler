package com.gs.cd.gscheduler.trigger.openfeign;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.trigger.vo.GschedulerTriggerVO;
import lombok.NonNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

/**
 * @Author seven
 * @Date 2021/4/27 14:17
 * @Description
 * @Version 1.0
 */
@Component
@FeignClient(name = "${gscheduler.api:gscheduler-trigger-server}")
public interface TriggerClient {
    @GetMapping(value = "/gtrigger")
    ApiResult getByTaskIdAndGroupName(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                      @NonNull @RequestParam String taskId,
                                      @NonNull @RequestParam String groupName);

    @PostMapping(value = "/gtrigger/create")
    public ApiResult create(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @NonNull @RequestBody GschedulerTriggerVO params);

    @PostMapping(value = "/gtrigger/delete")
    ApiResult delete(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                     @NonNull @RequestParam String taskId,
                     @NonNull @RequestParam String groupName);

    @PostMapping(value = "/gtrigger/update/{taskId}/{groupName}")
    ApiResult updateByTaskIdAndGroupName(
            @NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @NonNull @PathVariable String taskId,
            @NonNull @PathVariable String groupName,
            @NonNull @RequestBody GschedulerTriggerVO params);

    @GetMapping(value = "/gtrigger/suspend")
    ApiResult suspend(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                         @NonNull @RequestParam String taskId,
                                         @NonNull @RequestParam String groupName,
                                         @NonNull @RequestParam boolean isSuspend);
}

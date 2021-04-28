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
@FeignClient(name = "${gscheduler.server:gscheduler-server}")
public interface TriggerClient {
    @GetMapping(value = "/gscheduler/trigger")
    ApiResult getByTaskIdAndGroupName(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                      @NonNull String taskId,
                                      @NonNull String groupName);

    @PostMapping(value = "/gscheduler/trigger/create")
    public ApiResult create(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @NonNull @RequestBody GschedulerTriggerVO params);

    @PostMapping(value = "/gscheduler/trigger/delete")
    ApiResult delete(@NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                     @NonNull String taskId,
                     @NonNull String groupName);

    @PostMapping(value = "/gscheduler/trigger/update/{taskId}/{groupName}")
    ApiResult updateByTaskIdAndGroupName(
            @NonNull @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @NonNull @PathVariable String taskId,
            @NonNull @PathVariable String groupName,
            @NonNull @RequestBody GschedulerTriggerVO params);
}

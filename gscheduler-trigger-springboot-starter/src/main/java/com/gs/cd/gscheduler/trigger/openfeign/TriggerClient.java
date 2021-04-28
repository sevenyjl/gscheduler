package com.gs.cd.gscheduler.trigger.openfeign;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.gscheduler.trigger.vo.GschedulerTriggerVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author seven
 * @Date 2021/4/27 14:17
 * @Description
 * @Version 1.0
 */
@Component
@FeignClient(url = "${gscheduler.server:gscheduler-server}", name = "gscheduler-server")
public interface TriggerClient {
    @GetMapping(value = "/gscheduler/trigger")
    ApiResult getByTaskIdAndGroupName(String taskId, String groupName);

    @PostMapping(value = "/gscheduler/trigger/create")
    public ApiResult create(@RequestBody GschedulerTriggerVO params);

    @PostMapping(value = "/gscheduler/trigger/delete")
    ApiResult delete(String taskId, String groupName);

    @PostMapping(value = "/gscheduler/trigger/update/{taskId}/{groupName}")
    ApiResult updateByTaskIdAndGroupName(
            @PathVariable String taskId,
            @PathVariable String groupName,
            @RequestBody GschedulerTriggerVO params);
}

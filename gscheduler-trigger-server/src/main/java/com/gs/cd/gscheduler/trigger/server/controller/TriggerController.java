package com.gs.cd.gscheduler.trigger.server.controller;

import cn.hutool.core.net.Ipv4Util;
import com.alibaba.nacos.client.utils.IPUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.trigger.server.service.GschedulerTriggerService;
import com.gs.cd.gscheduler.trigger.server.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.trigger.server.service.impl.NacosService;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.netty.NettyWebServer;
import org.springframework.web.bind.annotation.*;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * @Author seven
 * @Date 2021/5/10 16:43
 * @Description 触发器
 * @Version 1.0
 */
@RestController
@RequestMapping("/gtrigger")
@Slf4j
public class TriggerController {

    @Autowired
    GschedulerTriggerService gschedulerTriggerService;
    @Autowired
    ServerProperties serverProperties;
    @Autowired
    NacosService nacosService;

    @GetMapping(value = "stopQuartz/{id}")
    boolean stopQuartzById(@PathVariable Integer id) {
        log.info("删除任务：{}", id);
        return gschedulerTriggerService.stopQuartzById(id);
    }

    @PostMapping(value = "addQuartz")
    boolean addQuartzById(@RequestBody GschedulerTrigger gschedulerTrigger) {
        log.info("添加任务：{}", gschedulerTrigger);
        return gschedulerTriggerService.addQuartzById(gschedulerTrigger);
    }

    @GetMapping(value = "health")
    ApiResult health() throws UnknownHostException {
        ApiResult success = ApiResult.success("gscheduler-trigger-server <version>1.1.0-SNAPSHOT</version>");
        success.put("ip", InetAddress.getLocalHost().getHostAddress());
        success.put("server list", nacosService.listAll());
        success.put("port", serverProperties.getPort());
        return success;
    }

    @GetMapping(value = "")
    ApiResult getByTaskIdAndGroupName(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                                      @RequestParam String taskId,
                                      @RequestParam String groupName) {
        log.info("get trigger tenantCode={},taskId={},groupName={}", tenantCode, taskId, groupName);
        GschedulerTrigger byTaskIdAndGroupName = gschedulerTriggerService.getByTaskIdAndGroupName(tenantCode, taskId, groupName);
        byTaskIdAndGroupName.params2ITrigger();
        return ApiResult.success(byTaskIdAndGroupName);
    }


    @PostMapping(value = "/create")
    public ApiResult create(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @RequestBody GschedulerTrigger gschedulerTrigger) {
        log.info("create trigger tenantCode={},gschedulerTrigger={}", tenantCode, gschedulerTrigger);
        gschedulerTrigger.setTenantCode(tenantCode);
        gschedulerTrigger.iTrigger2Params();
        return gschedulerTriggerService.create(gschedulerTrigger) ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/delete")
    ApiResult delete(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                     @RequestParam String taskId,
                     @RequestParam String groupName) {
        log.info("delete trigger tenantCode={},taskId={},groupName={}", tenantCode, taskId, groupName);
        return gschedulerTriggerService.delete(tenantCode, taskId, groupName) ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/update/{taskId}/{groupName}")
    ApiResult updateByTaskIdAndGroupName(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String taskId,
            @PathVariable String groupName,
            @RequestBody GschedulerTrigger gschedulerTrigger) {
        log.info("update trigger tenantCode={},taskId={},groupName={},gschedulerTrigger={}",
                tenantCode, taskId, groupName, gschedulerTrigger);
        gschedulerTrigger.setTaskId(taskId);
        gschedulerTrigger.setGroupName(groupName);
        gschedulerTrigger.setTenantCode(tenantCode);
        gschedulerTrigger.iTrigger2Params();
        return gschedulerTriggerService.edit(gschedulerTrigger) ? ApiResult.success() : ApiResult.error();
    }
}
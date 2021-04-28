package com.gs.cd.gscheduler.server.controller;

import cn.hutool.core.util.StrUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gscheduler.server.quartz.QuartzExecutors;
import org.springframework.web.bind.annotation.*;
import com.gs.cd.gscheduler.dao.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.server.service.impl.GschedulerTriggerService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
@RestController
@RequestMapping("/gscheduler/trigger")
public class GschedulerTriggerController {

    @Autowired
    QuartzExecutors quartzExecutors;

    @Autowired
    private GschedulerTriggerService gschedulerTriggerService;


    @GetMapping(value = "/{id}")
    public ApiResult getById(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                             @PathVariable("id") String id) {
        return ApiResult.success(gschedulerTriggerService.getById(id));
    }

    @GetMapping(value = "/gscheduler/trigger")
    ApiResult getByTaskIdAndGroupName(String taskId, String groupName) {
        return ApiResult.success(gschedulerTriggerService.getByTaskIdAndGroupName(taskId, groupName));
    }

    @PostMapping(value = "/create")
    public ApiResult create(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @RequestBody GschedulerTrigger params) {
        params.setUpdateTime(new Date());
        params.setCreateTime(new Date());
        boolean b = gschedulerTriggerService.save(params);
        if (b) {
            // TODO: 2021/4/27 转移到server层 并 校验corn表达式
            quartzExecutors.addJob(params);
        }
        return b ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/delete/{id}")
    public ApiResult delete(@RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
                            @PathVariable("id") String id) {
        GschedulerTrigger byId = gschedulerTriggerService.getById(id);
        if (byId == null) {
            return ApiResult.error(String.format("不存在id=%s，的数据", id));
        }
        boolean b = gschedulerTriggerService.removeById(id);
        if (b) {
            // TODO: 2021/4/27 转移到server层 并 校验corn表达式
            quartzExecutors.deleteJob(byId.getTaskId(), byId.getGroupName());
        }
        return b ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/delete")
    public ApiResult delete(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode, String taskId, String groupName) {
        GschedulerTrigger byId = gschedulerTriggerService.getByTaskIdAndGroupName(taskId, groupName);
        if (byId == null) {
            return ApiResult.error(String.format("不存在taskId=%s;groupName=%s，的数据", taskId, groupName));
        }
        boolean b = gschedulerTriggerService.removeById(byId.getId());
        if (b) {
            // TODO: 2021/4/27 转移到server层 并 校验corn表达式
            quartzExecutors.deleteJob(taskId, groupName);
        }
        return b ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/update")
    public ApiResult update(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode, @RequestBody GschedulerTrigger params) {
        params.setUpdateTime(new Date());
        boolean b = gschedulerTriggerService.updateById(params);
        if (b) {
            // TODO: 2021/4/27 转移到server层 并 校验corn表达式 先更新再删除？！ 还是直接就支持更新 待验证
            quartzExecutors.addJob(params);
        }
        return b ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/update/{taskId}/{groupName}")
    public ApiResult updateByTaskIdAndGroupName(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode,
            @PathVariable String taskId,
            @PathVariable String groupName,
            @RequestBody GschedulerTrigger params) {
        GschedulerTrigger byTaskIdAndGroupName = gschedulerTriggerService.getByTaskIdAndGroupName(taskId, groupName);
        if (byTaskIdAndGroupName == null) {
            return ApiResult.error(String.format("未找到taskId=%s,groupName=%s的数据", taskId, groupName));
        }
        params.setId(byTaskIdAndGroupName.getId());
        params.setUpdateTime(new Date());
        boolean b = gschedulerTriggerService.updateById(params);
        if (b) {
            // TODO: 2021/4/27 转移到server层 并 校验corn表达式 先更新再删除？！ 还是直接就支持更新 待验证
            quartzExecutors.addJob(params);
        }
        return b ? ApiResult.success() : ApiResult.error();
    }
}

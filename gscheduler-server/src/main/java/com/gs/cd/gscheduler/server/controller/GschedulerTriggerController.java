package com.gs.cd.gscheduler.server.controller;

import com.gs.cd.cloud.common.ApiResult;
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
    public ApiResult getById(@PathVariable("id") String id) {
        return ApiResult.success(gschedulerTriggerService.getById(id));
    }

    @PostMapping(value = "/create")
    public ApiResult create(@RequestBody GschedulerTrigger params) {
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
    public ApiResult delete(@PathVariable("id") String id) {
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
    public ApiResult delete(String taskId, String groupName) {
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
    public ApiResult update(@RequestBody GschedulerTrigger params) {
        params.setUpdateTime(new Date());
        boolean b = gschedulerTriggerService.updateById(params);
        if (b) {
            // TODO: 2021/4/27 转移到server层 并 校验corn表达式 先更新再删除？！ 还是直接就支持更新 待验证
            quartzExecutors.addJob(params);
        }
        return b ? ApiResult.success() : ApiResult.error();
    }
}

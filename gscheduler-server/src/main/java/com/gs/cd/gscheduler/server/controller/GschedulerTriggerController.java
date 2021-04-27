package com.gs.cd.gscheduler.server.controller;

import com.gs.cd.cloud.common.ApiResult;
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
        return b ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/delete/{id}")
    public ApiResult delete(@PathVariable("id") String id) {
        boolean b = gschedulerTriggerService.removeById(id);
        return b ? ApiResult.success() : ApiResult.error();
    }

    @PostMapping(value = "/update")
    public ApiResult update(@RequestBody GschedulerTrigger params) {
        params.setUpdateTime(new Date());
        boolean b = gschedulerTriggerService.updateById(params);
        return b ? ApiResult.success() : ApiResult.error();
    }
}

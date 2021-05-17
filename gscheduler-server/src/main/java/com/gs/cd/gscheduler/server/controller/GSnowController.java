package com.gs.cd.gscheduler.server.controller;

import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.common.HttpHeadersParam;
import com.gs.cd.gsnow.api.GSnowClient;
import com.gs.cd.gsnow.api.service.GSnowService;
import com.gs.cd.gsnow.entity.GSnowCollector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * schedulerx
 *
 * @Author seven
 * @Date 2021/2/5 15:53
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("gsnow")
public class GSnowController {
    @Autowired
    GSnowClient gSnowClient;

    /**
     * 获取收集器集合
     *
     * @return
     */
    @GetMapping("collector/list")
    public ApiResult listEtlCollector(
            @RequestHeader(HttpHeadersParam.TENANT_CODE) String tenantCode) {
        ApiResult apiResult = gSnowClient.search(new GSnowCollector(), tenantCode);
        return apiResult;
    }
}

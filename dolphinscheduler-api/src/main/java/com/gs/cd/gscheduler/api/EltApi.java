package com.gs.cd.gscheduler.api;

import com.gs.cd.gscheduler.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @Author seven
 * @Date 2021/5/13 17:00
 * @Description
 * @Version 1.0
 */
@FeignClient(url = "${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}", name = "EltApi")
public interface EltApi {
    @GetMapping(value = "/etl/collector/list")
    public Result listEtlCollector();
}


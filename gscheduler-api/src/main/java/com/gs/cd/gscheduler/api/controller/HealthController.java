package com.gs.cd.gscheduler.api.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 探活接口
 * @Author seven
 * @Date 2021/4/29 17:23
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("gscheduler")
public class HealthController {
    @RequestMapping("health")
    public String health() {
        return "gscheduler-api success";
    }
}

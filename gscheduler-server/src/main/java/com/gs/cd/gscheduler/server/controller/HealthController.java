package com.gs.cd.gscheduler.server.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author seven
 * @Date 2021/5/7 10:53
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("")
public class HealthController {

    @RequestMapping("/health")
    public String health() {
        return "gscheduler is success~";
    }
}

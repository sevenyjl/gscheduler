package com.gs.cd.gscheduler.server.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author seven
 * @Date 2021/5/7 10:53
 * @Description
 * @Version 1.0
 */
@RestController
@RequestMapping("")
public class HealthApi {
    @Value("${dolphinscheduler.tenantName:default}")
    private String tenantName;
    @Value("${dolphinscheduler.username:admin}")
    private String username;
    @Value("${dolphinscheduler.password:dolphinscheduler123}")
    private String password;
    @Value("${dolphinscheduler.url:http://127.0.0.1:12345/dolphinscheduler}")
    private String url;

    @RequestMapping("/health")
    public String health() {
        return "gscheduler is success~";
    }

    @RequestMapping("/dolphinscheduler/check")
    public Map<String, Object> dolphinschedulerCheck() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("url", url);
        result.put("username", username);
        result.put("password", password);
        result.put("tenantName", tenantName);
        return result;
    }
}

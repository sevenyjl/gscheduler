package com.gs.cd.gscheduler.trigger.server.entity;

import lombok.Data;

/**
 * @Author seven
 * @Date 2021/5/14 23:24
 * @Description
 * @Version 1.0
 */
@Data
public class NacosParamsTrigger {
    private String url;
    private String serviceName;
    private String nameSpace = "public";
    private String groupName = "DEFAULT_GROUP";
}

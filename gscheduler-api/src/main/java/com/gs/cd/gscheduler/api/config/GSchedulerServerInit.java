package com.gs.cd.gscheduler.api.config;

import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.quartz.QuartzExecutors;
import com.gs.cd.gscheduler.api.service.GschedulerTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class GSchedulerServerInit implements ApplicationRunner {

    @Autowired
    GschedulerTriggerService gschedulerTriggerService;
    @Autowired
    QuartzExecutors quartzExecutors;

    public void TriggerInit() {
        log.info("*******\tinit GSchedulerTrigger corn task\t*******");
        List<String> tenantCodeList = gschedulerTriggerService.listAllTenantCode();
        // TODO: 2021/4/28 记录一个可能得问题  如果说 sys_tenant_own_store 中的租户不在同一个pg上 可能存在查询失败的问题
        HashMap<String, String> errorMsg = new HashMap<>();
        tenantCodeList.forEach(tenantCode -> {
            List<GschedulerTrigger> gschedulerTriggers = new ArrayList<>();
            try {
                gschedulerTriggers = gschedulerTriggerService.listByTenantCode(tenantCode);
            } catch (Exception e) {
                errorMsg.put(tenantCode, e.getMessage());
            }
            gschedulerTriggers.forEach(t -> {
                try {
                    quartzExecutors.addJob(tenantCode, t);
                } catch (Exception e) {
                    errorMsg.put(String.format("tenantCode=%s data=> %s", tenantCode, t.toString()), e.getMessage());
                }
            });
        });
        if (!errorMsg.isEmpty()) {
            log.error("!!!!!!!!!!!!!!!初始化触发器错误：!!!!!!!!!!!!!!");
            errorMsg.forEach((k, v) -> {
                log.error("错误数据:{}，错误详情:{}", k, v);
            });
            System.exit(-1);
        }
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        TriggerInit();

    }

}
package com.gs.cd.gscheduler.api.config;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.utils.ApiResultParserUtils;
import com.gs.cd.cloud.utils.JsonUtils;
import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.quartz.QuartzExecutors;
import com.gs.cd.gscheduler.api.service.GschedulerTriggerService;
import com.gs.cd.kmp.api.AuthClient;
import lombok.SneakyThrows;
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
    @Autowired
    AuthClient authClient;

    public void triggerInit() throws Exception {
        log.info("*******\tinit GSchedulerTrigger corn task\t*******");
        try {
            ApiResult apiResult = authClient.listAllValidTenantCode();
            if (!apiResult.isSuccess()) {
                log.error("调用 authClient.listAllValidTenantCode 错误：" + apiResult);
                System.exit(-1);
            }
            // TODO: 2021/4/28 记录一个可能得问题  如果说 sys_tenant_own_store 中的租户不在同一个pg上 可能存在查询失败的问题
            HashMap<String, String> errorMsg = new HashMap<>();
            JSONUtil.parseArray(apiResult.getData()).forEach(tenantCode -> {
                List<GschedulerTrigger> gschedulerTriggers = new ArrayList<>();
                try {
                    gschedulerTriggers = gschedulerTriggerService.listByTenantCode(tenantCode.toString());
                } catch (Exception e) {
                    errorMsg.put(tenantCode.toString(), e.getMessage());
                }
                gschedulerTriggers.forEach(t -> {
                    try {
                        quartzExecutors.addJob(tenantCode.toString(), t);
                    } catch (Exception e) {
                        errorMsg.put(String.format("tenantCode=%s data=> %s", tenantCode.toString(), t.toString()), e.getMessage());
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
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    @Override
    public void run(ApplicationArguments arg0) throws Exception {
        triggerInit();
    }

}
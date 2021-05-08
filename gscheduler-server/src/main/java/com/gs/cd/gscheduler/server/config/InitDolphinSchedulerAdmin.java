package com.gs.cd.gscheduler.server.config;

import com.gs.cd.gscheduler.api.LoginApi;
import com.gs.cd.gscheduler.api.TenantApi;
import com.gs.cd.gscheduler.entity.Tenant;
import com.gs.cd.gscheduler.enums.Status;
import com.gs.cd.gscheduler.server.cache.TenantCodeService;
import com.gs.cd.gscheduler.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author seven
 * @Date 2021/4/13 17:43
 * @Description
 * @Version 1.0
 */
@Component
public class InitDolphinSchedulerAdmin implements ApplicationRunner {
    @Value("${dolphinscheduler.username:admin}")
    private String username;
    @Value("${dolphinscheduler.password:dolphinscheduler123}")
    private String password;
    @Value("${dolphinscheduler.tenantName:default}")
    private String tenantName;
    @Autowired
    LoginApi loginApi;
    @Autowired
    TenantApi tenantApi;
    @Autowired
    TenantCodeService tenantCodeService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Result login = loginApi.login(username, password);
        if (Status.valueOfWithCode(login.getCode()) != Status.SUCCESS) {
            throw new RuntimeException(String.format("初始化调度管理用户错误。用户名：%s，密码：%s", username, password));
        }
        String sessionId = login.getData().toString()
                .replace("{", "").replace("}", "");
        tenantCodeService.setAdminSessionId(sessionId);
        //查看是否有默认租户
        Result<List<Tenant>> result = tenantApi.queryTenantlist(sessionId);
        Tenant tenant = null;
        if (!result.getData().isEmpty()) {
            tenant = result.getData().stream().filter(s -> s.getTenantCode().equals(tenantName)).findFirst().orElse(null);
        }
        if (tenant == null) {
            tenantApi.createTenant(sessionId, tenantName, tenantName, 1, "默认租户");
            tenant = tenantApi.queryTenantlist(sessionId).getData().stream().filter(s -> s.getTenantCode().equals(tenantName)).findFirst().orElse(null);
        }
        tenantCodeService.setTenant(tenant);
    }
}

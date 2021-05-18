package com.gs.cd.gscheduler.server.cache;

import com.gs.cd.gscheduler.api.LoginApi;
import com.gs.cd.gscheduler.api.UsersApi;
import com.gs.cd.gscheduler.entity.Tenant;
import com.gs.cd.gscheduler.entity.User;
import com.gs.cd.gscheduler.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/4/13 17:17
 * @Description
 * @Version 1.0
 */
@Slf4j
@Component
public class TenantCodeService {
    private static final HashMap<String, String> tenantCodeSessionMap = new HashMap<>();

    @Value("${dolphinscheduler.password:gs123456}")
    private String defaultPassword;

    private static String adminSessionId = null;

    public void setAdminSessionId(String adminSessionId) {
        if (TenantCodeService.adminSessionId == null) {
            TenantCodeService.adminSessionId = adminSessionId;
        }
    }

    @Autowired
    UsersApi usersApi;
    @Autowired
    LoginApi loginApi;

    public static String getSessionId(String tenantCode) {
        return tenantCodeSessionMap.get(tenantCode);
    }

    private static int runTimes = 0;

    public String check(String tenantCode) {
        String sessionId = tenantCodeSessionMap.get(tenantCode);
        if (sessionId == null) {
            //登录
            Result login = loginApi.login(tenantCode, defaultPassword);
            if (login.getCode() == 10013) {
                //检测用户并创建
                // TODO: 2021/4/13 创建用户逻辑应该在创建租户那里做
                Result<List<User>> result = usersApi.listUser(adminSessionId);
                User user = result.getData().stream().filter(s -> s.getUserName().equals(tenantCode)).findFirst().orElse(null);
                if (user == null) {
                    //创建
                    usersApi.createUser(adminSessionId, tenantCode, defaultPassword, tenant.getId(), null, "test@test.com", null);
                } else {
                    //修改密码
                    usersApi.updateUser(adminSessionId, user.getId(), user.getUserName(), defaultPassword, user.getQueue(), user.getEmail(), user.getTenantId(), null);
                }
                if (runTimes > 10) {
                    log.error("重试次数过多了，请联系程序员修复。重试次数：{}", runTimes);
                }
                runTimes++;
                return check(tenantCode);
            } else {
                runTimes = 0;
                sessionId = login.getData().toString()
                        .replace("{", "").replace("}", "");
                tenantCodeSessionMap.put(tenantCode, sessionId + ";language=zh_CN");
                return sessionId;
            }
        } else {
            // TODO: 2021/4/13 check用户有效性
            return sessionId;
        }
    }

    private static Tenant tenant = null;

    public void setTenant(Tenant tenant) {
        if (TenantCodeService.tenant == null) {
            TenantCodeService.tenant = tenant;
        }
    }
}

package com.gs.cd.gscheduler.server.service.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.kmp.api.AuthClient;
import com.gs.cd.kmp.api.enums.ResourceCategoryEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/5/27 16:18
 * @Description
 * @Version 1.0
 */
@Service
@Slf4j
public class PurviewCheckService {

    @Autowired
    AuthClient authClient;

    public ApiResult check(String module, @NonNull String perms, @NonNull String token, @NonNull String tenantCode) {
        //获取当前用户的所有权限
        ApiResult loginUserAllResource = authClient.listLoginUserAllResource(ResourceCategoryEnum.TENANT, token, tenantCode);
        log.debug("请求结果：{}", loginUserAllResource.getData());
        if (loginUserAllResource.isSuccess()) {
            ArrayList tenantResource = (ArrayList) loginUserAllResource.getData();
            for (Object s : tenantResource) {
                JSONObject jsonObject = JSONUtil.parseObj(s);
                if (perms.equals(jsonObject.getStr("perms"))) {
                    return ApiResult.success();
                }
            }
            throw new RuntimeException("当前用户无操作[" + module + "]权限");
        } else {
            log.error("请求错误/auth/user/list/resource 错误信息:{}", loginUserAllResource);
            throw new RuntimeException("权限验证异常："+loginUserAllResource.getMsg());
        }
    }

    public ApiResult check(String module, @NonNull List<String> permsList, @NonNull String token, @NonNull String tenantCode) {
        //获取当前用户的所有权限
        ApiResult loginUserAllResource = authClient.listLoginUserAllResource(ResourceCategoryEnum.TENANT, token, tenantCode);
        log.debug("请求结果：{}", loginUserAllResource.getData());
        if (loginUserAllResource.isSuccess()) {
            ArrayList tenantResource = (ArrayList) loginUserAllResource.getData();
            for (Object s : tenantResource) {
                JSONObject jsonObject = JSONUtil.parseObj(s);
                if (permsList.contains(jsonObject.getStr("perms"))) {
                    return ApiResult.success();
                }
            }
            throw new RuntimeException("当前用户无操作[" + module + "]权限");
        } else {
            log.error("请求错误/auth/user/list/resource 错误信息:{}", loginUserAllResource);
            throw new RuntimeException("当前用户无操作[" + module + "]权限");
        }
    }
}

package com.gs.cd.gscheduler.server.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.kmp.api.entity.Resource;
import com.gs.cd.gscheduler.server.entity.GschedulerProjectPurview;
import com.gs.cd.gscheduler.server.mapper.GschedulerProjectPurviewMapper;
import com.gs.cd.gscheduler.server.service.GschedulerProjectPurviewService;
import com.gs.cd.kmp.api.AuthClient;
import com.gs.cd.kmp.api.enums.ResourceCategoryEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author seven
 * @Date 2021/5/13 17:41
 * @Description
 * @Version 1.0
 */
@Service
@Slf4j
public class GschedulerProjectPurviewServiceImpl extends ServiceImpl<GschedulerProjectPurviewMapper, GschedulerProjectPurview> implements GschedulerProjectPurviewService {

    @Autowired
    AuthClient authClient;
    private static final List<String> permsList = Arrays.asList(
//            GschedulerProjectPurview.add,
            GschedulerProjectPurview.configurePermissions,
            GschedulerProjectPurview.delete,
            GschedulerProjectPurview.edit,
            GschedulerProjectPurview.view);


    @Override
    public Collection<Resource> getResources(List<GschedulerProjectPurview> gschedulerProjectPurviews, String token, String tenantCode) {
        HashSet<Resource> resourcesSet = new HashSet<>();
        //获取当前用户的所有权限
        ApiResult loginUserAllResource = authClient.listLoginUserAllResource(ResourceCategoryEnum.TENANT, token, tenantCode);
        if (loginUserAllResource.isSuccess()) {
            ArrayList tenantResource = (ArrayList) loginUserAllResource.getData();
            //只获取tenant层级
//            ArrayList tenantResource = (ArrayList) data.getOrDefault("tenant", new ArrayList<>());
            tenantResource.forEach(s -> {
                JSONObject jsonObject = JSONUtil.parseObj(s);
                if (permsList.contains(jsonObject.getStr("perms"))) {
                    Resource resource = underCase2CamelCase(jsonObject).toBean(Resource.class);
                    resourcesSet.add(resource);
                }
            });
            log.debug("请求结果：{}", loginUserAllResource.getData());
        } else {
            log.error("请求错误/auth/user/list/resource 错误信息:{}", loginUserAllResource);
            throw new RuntimeException(loginUserAllResource.getMsg());
        }
        //查询所有角色
        if (gschedulerProjectPurviews.isEmpty()) {
            return resourcesSet;
        }
        HashSet<String> roleIds = new HashSet<>();
        gschedulerProjectPurviews.forEach(s -> {
            //查询
            String roleId = s.getRoleId();
            roleIds.add(roleId);
        });
        ApiResult apiResult = authClient.listByRoleIds(roleIds);
        if (apiResult.isSuccess()) {
            log.debug("请求结果：{}", apiResult);
            JSONArray jsonArray = JSONUtil.parseArray(apiResult.getData());
            jsonArray.forEach(s -> {
                JSONObject jb = (JSONObject) s;
                String perms = jb.getStr("perms");
                if (permsList.contains(perms)) {
                    Resource resource = underCase2CamelCase(jb).toBean(Resource.class);
                    resourcesSet.add(resource);
                }
            });
            return resourcesSet;
        } else {
            log.error("请求错误/auth/resource/group/list/roleId 错误信息:{}", apiResult);
            throw new RuntimeException(apiResult.getMsg());
        }
    }

    @Override
    public List<GschedulerProjectPurview> listByProjectId(Integer projectId) {
        return list(new QueryWrapper<GschedulerProjectPurview>().lambda().eq(GschedulerProjectPurview::getProjectId, projectId));
    }

    //是否开启业务权限配置
    @Value("${gscheduler.purview.flag:true}")
    private boolean purviewFlag = true;

    @Override
    public void check(Integer id, @NonNull String resourcesParams, String token, String tenantCode) {
        if (purviewFlag) {
            List<GschedulerProjectPurview> gschedulerProjectPurviews = listByProjectId(id);
            Collection<Resource> resourcesByProjectId = getResources(gschedulerProjectPurviews, token, tenantCode);
            Resource resource = resourcesByProjectId.stream().filter(s -> s.getPerms().equals(resourcesParams)).findFirst().orElse(null);
            if (resource == null) {
                log.error("参数：projectId={},resourcesParams={},tenantCode={},当前用户权限={}", id, resourcesParams, tenantCode, resourcesByProjectId);
                throw new RuntimeException("当前用户无权限操作");
            }
        }
    }

    @Override
    public void check(String projectName, @NonNull String resourcesParams, String token, String tenantCode) {
        if (purviewFlag) {
            List<GschedulerProjectPurview> resourcesByProjectName = getResourcesByProjectName(projectName);
            Collection<Resource> resourcesByProjectId = getResources(resourcesByProjectName, token, tenantCode);
            Resource resource = resourcesByProjectId.stream().filter(s -> s.getPerms().equals(resourcesParams)).findFirst().orElse(null);
            if (resource == null) {
                log.error("参数：projectId={},resourcesParams={},tenantCode={},当前用户权限={}", resourcesByProjectName, resourcesParams, tenantCode, resourcesByProjectId);
                throw new RuntimeException("当前用户无权限操作");
            }
        }
    }
    @Override
    public List<GschedulerProjectPurview> getResourcesByProjectName(String projectName) {
        return list(new QueryWrapper<GschedulerProjectPurview>().lambda().eq(GschedulerProjectPurview::getProjectName, projectName));
    }

    @Override
    public boolean removeByProjectId(Integer projectId) {
        return remove(new QueryWrapper<GschedulerProjectPurview>().lambda().eq(GschedulerProjectPurview::getProjectId, projectId));
    }

    private JSONObject underCase2CamelCase(JSONObject jsonObject) {
        JSONObject result = new JSONObject();
        jsonObject.forEach((k, v) -> {
            String s = StrUtil.toCamelCase(k);
            result.set(s, v);
        });
        return result;
    }
}

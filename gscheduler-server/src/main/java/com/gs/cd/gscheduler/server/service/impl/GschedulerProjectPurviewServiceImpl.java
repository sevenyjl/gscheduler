package com.gs.cd.gscheduler.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.kmp.api.entity.Resource;
import com.gs.cd.gscheduler.server.entity.GschedulerProjectPurview;
import com.gs.cd.gscheduler.server.mapper.GschedulerProjectPurviewMapper;
import com.gs.cd.gscheduler.server.service.GschedulerProjectPurviewService;
import com.gs.cd.kmp.api.AuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

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

    @Override
    public List<Resource> getResourcesByProjectId(Integer id, String token, String tenantCode) {
        //查询所有角色
        List<GschedulerProjectPurview> gschedulerProjectPurviews = listByProjectId(id);
        HashSet<String> roleIds = new HashSet<>();
        gschedulerProjectPurviews.forEach(s -> {
            //查询
            String roleId = s.getRoleId();
            roleIds.add(roleId);
        });
        ApiResult apiResult = authClient.listByRoleCode(tenantCode, roleIds);
        if (apiResult.isSuccess()) {
            apiResult.getData();
            log.debug("请求结果：{}", apiResult);
        } else {
            log.error("请求错误/auth/resource/group/list/roleCode/{tenantCode} 错误信息:{}", apiResult);
        }
        return null;
    }

    public List<GschedulerProjectPurview> listByProjectId(Integer projectId) {
        return list(new QueryWrapper<GschedulerProjectPurview>().lambda().eq(GschedulerProjectPurview::getProjectId, projectId));
    }
}

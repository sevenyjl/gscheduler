package com.gs.cd.gscheduler.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.gscheduler.server.entity.GschedulerProjectPurview;
import com.gs.cd.kmp.api.entity.Resource;

import java.util.Collection;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/5/13 17:40
 * @Description
 * @Version 1.0
 */
public interface GschedulerProjectPurviewService extends IService<GschedulerProjectPurview> {
    Collection<Resource> getResourcesByProjectId(Integer id, String token, String tenantCode);
}

package com.gs.cd.gscheduler.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.dao.mapper.GschedulerTriggerMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.gscheduler.server.service.GschedulerTriggerService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
@Service
public class GschedulerTriggerServiceImpl extends ServiceImpl<GschedulerTriggerMapper, GschedulerTrigger> implements GschedulerTriggerService {

    public void add2Quartz(GschedulerTrigger gschedulerTrigger) {

    }

    @Override
    public GschedulerTrigger getByTaskIdAndGroupName(String taskId, String groupName) {
        return getOne(new QueryWrapper<GschedulerTrigger>().
                lambda().eq(GschedulerTrigger::getTaskId, taskId).eq(GschedulerTrigger::getGroupName, groupName));
    }

    @Override
    public List<String> listAllTenantCode() {
        return baseMapper.listAllTenantCode();
    }

    @Override
    public List<GschedulerTrigger> listByTenantCode(String tenantCode) {
        return baseMapper.listByTenantCode(tenantCode);
    }
}

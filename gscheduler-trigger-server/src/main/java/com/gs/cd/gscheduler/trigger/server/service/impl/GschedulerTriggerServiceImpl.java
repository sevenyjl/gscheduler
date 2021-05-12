package com.gs.cd.gscheduler.trigger.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.gscheduler.trigger.server.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.trigger.server.execption.TriggerException;
import com.gs.cd.gscheduler.trigger.server.mapper.GschedulerTriggerMapper;
import com.gs.cd.gscheduler.trigger.server.quartz.QuartzExecutors;
import com.gs.cd.gscheduler.trigger.server.service.GschedulerTriggerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class GschedulerTriggerServiceImpl extends ServiceImpl<GschedulerTriggerMapper, GschedulerTrigger> implements GschedulerTriggerService {
    @Autowired
    QuartzExecutors quartzExecutors;

    @Override
    public boolean create(GschedulerTrigger gschedulerTrigger) {
        //创建定时任务
        try {
            quartzExecutors.addJob(gschedulerTrigger);
        } catch (TriggerException e) {
            log.error(e.getMessage());
            return false;
        }
        //创建表
        gschedulerTrigger.setLockFlag(true);
        return save(gschedulerTrigger);
    }

    @Override
    public GschedulerTrigger getByTaskIdAndGroupName(String tenantCode, String taskId, String groupName) {
        return getOne(new QueryWrapper<GschedulerTrigger>().lambda()
                .eq(GschedulerTrigger::getTenantCode, tenantCode)
                .eq(GschedulerTrigger::getTaskId, taskId)
                .eq(GschedulerTrigger::getGroupName, groupName));
    }

    @Override
    public boolean delete(String tenantCode, String taskId, String groupName) {
        //去掉定时任务
        if (quartzExecutors.deleteJob(tenantCode, taskId, groupName)) {
            return remove(new QueryWrapper<GschedulerTrigger>().lambda().eq(GschedulerTrigger::getTenantCode, tenantCode)
                    .eq(GschedulerTrigger::getTaskId, taskId).eq(GschedulerTrigger::getGroupName, groupName));
        }
        return false;
    }

    @Override
    public boolean edit(GschedulerTrigger gschedulerTrigger) {
        GschedulerTrigger byTaskIdAndGroupName = getByTaskIdAndGroupName(gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName());
        if (byTaskIdAndGroupName == null) {
            throw new RuntimeException(String.format("未找到tenantCode=%s,taskId=%s,groupName=%s的触发数据", gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName()));
        }
        if (quartzExecutors.deleteJob(gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName())) {
            try {
                quartzExecutors.addJob(gschedulerTrigger);
                gschedulerTrigger.setId(byTaskIdAndGroupName.getId());
                gschedulerTrigger.setLockFlag(true);
                updateById(gschedulerTrigger);
            } catch (TriggerException e) {
                log.error(e.getMessage());
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean lockById(GschedulerTrigger gschedulerTrigger) {
        gschedulerTrigger.setLockFlag(true);
        return updateById(gschedulerTrigger);
    }

    @Override
    public boolean lockBathById(List<GschedulerTrigger> gschedulerTriggerList) {
        gschedulerTriggerList.forEach(s -> s.setLockFlag(true));
        return saveBatch(gschedulerTriggerList);
    }

    @Override
    public boolean unlockById(GschedulerTrigger gschedulerTrigger) {
        gschedulerTrigger.setLockFlag(false);
        return updateById(gschedulerTrigger);
    }

    @Override
    public boolean unlockBathById(List<GschedulerTrigger> gschedulerTriggerList) {
        gschedulerTriggerList.forEach(s -> s.setLockFlag(false));
        return saveBatch(gschedulerTriggerList);
    }
}

package com.gs.cd.gscheduler.trigger.server.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.gscheduler.trigger.server.config.GSchedulerTriggerInit;
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
    @Autowired
    NacosService nacosService;

    @Override
    public boolean create(GschedulerTrigger gschedulerTrigger) {
        //check
        GschedulerTrigger byTaskIdAndGroupName = getByTaskIdAndGroupName(gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName());
        if (byTaskIdAndGroupName != null)
            throw new RuntimeException(String.format("已经存在 tenantCode=%s,taskId=%s,groupName=%s的触发数据", gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName()));
        //创建定时任务
        try {
            if (!remoteAdd(nacosService.getFirstServerAddress(), gschedulerTrigger)) {
                return false;
            }
        } catch (TriggerException e) {
            log.error(e.getMessage());
            return false;
        }
        //创建表
        gschedulerTrigger.setLockFlag(true);
        gschedulerTrigger.setAddress(nacosService.getFirstServerAddress());
        if (save(gschedulerTrigger)) {
            return true;
        }
        return false;
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
        GschedulerTrigger byTaskIdAndGroupName = getByTaskIdAndGroupName(tenantCode, taskId, groupName);
        if (byTaskIdAndGroupName != null) {
            return remoteStop(byTaskIdAndGroupName.getAddress(), byTaskIdAndGroupName.getId());
        }
        return false;
    }

    @Override
    public boolean edit(GschedulerTrigger gschedulerTrigger) {
        GschedulerTrigger byTaskIdAndGroupName = getByTaskIdAndGroupName(gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName());
        if (byTaskIdAndGroupName == null) {
            throw new RuntimeException(String.format("未找到tenantCode=%s,taskId=%s,groupName=%s的触发数据", gschedulerTrigger.getTenantCode(), gschedulerTrigger.getTaskId(), gschedulerTrigger.getGroupName()));
        }
        try {
            if (!remoteStop(byTaskIdAndGroupName.getAddress(), byTaskIdAndGroupName.getId())) {
                log.error("远程停止quartz错误，{}", byTaskIdAndGroupName);
                return false;
            }
        } catch (Exception e) {
            log.error("远程停止quartz错误，{} \n错误详情:{}", byTaskIdAndGroupName, e.getMessage());
            return false;
        }
        try {
            if (!remoteAdd(nacosService.getFirstServerAddress(), gschedulerTrigger)) {
                log.error("远程添加quartz错误：{}", byTaskIdAndGroupName);
                return false;
            }
            gschedulerTrigger.setId(byTaskIdAndGroupName.getId());
            gschedulerTrigger.setLockFlag(true);
            gschedulerTrigger.setAddress(nacosService.getFirstServerAddress());
            return updateById(gschedulerTrigger);
        } catch (TriggerException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean lockById(GschedulerTrigger gschedulerTrigger) {
        gschedulerTrigger.setLockFlag(true);
        gschedulerTrigger.setAddress(nacosService.getFirstServerAddress());
        return updateById(gschedulerTrigger);
    }

    @Override
    public boolean lockBathById(List<GschedulerTrigger> gschedulerTriggerList) {
        if (CollectionUtil.isNotEmpty(gschedulerTriggerList)) {
            gschedulerTriggerList.forEach(s -> {
                s.setLockFlag(true);
                s.setAddress(nacosService.getFirstServerAddress());
            });
            return updateBatchById(gschedulerTriggerList);
        }
        return false;
    }

    @Override
    public boolean unlockById(GschedulerTrigger gschedulerTrigger) {
        gschedulerTrigger.setLockFlag(false);
        gschedulerTrigger.setAddress("");
        return updateById(gschedulerTrigger);
    }

    @Override
    public boolean unlockBathById(List<GschedulerTrigger> gschedulerTriggerList) {
        if (CollectionUtil.isNotEmpty(gschedulerTriggerList)) {
            gschedulerTriggerList.forEach(s -> {
                s.setLockFlag(false);
                s.setAddress("");
            });
            return updateBatchById(gschedulerTriggerList);
        }
        return false;
    }

    @Override
    public boolean stopQuartzById(Integer id) {
        GschedulerTrigger byId = getById(id);
        if (byId != null) {
            boolean b = quartzExecutors.deleteJob(byId.getTenantCode(), byId.getTaskId(), byId.getGroupName());
            log.debug("删除定时任务状态：{}，删除数据：{}", b, byId);
            return removeById(byId.getId());
        }
        return false;
    }

    @Override
    public boolean addQuartzById(GschedulerTrigger gschedulerTrigger) {
        if (gschedulerTrigger != null) {
            GSchedulerTriggerInit.addCache(gschedulerTrigger);
            quartzExecutors.addJob(gschedulerTrigger);
            return true;
        }
        return false;
    }

    private boolean remoteStop(String address, Integer id) {
        final String api = address + "/gtrigger/stopQuartz/" + id;
        return Boolean.parseBoolean(HttpUtil.get(api));
    }

    private boolean remoteAdd(String address, GschedulerTrigger gschedulerTrigger) {
        final String api = address + "/gtrigger/addQuartz";
        String post = HttpUtil.post(api, JSONUtil.toJsonStr(gschedulerTrigger));
        return Boolean.parseBoolean(post);
    }
}

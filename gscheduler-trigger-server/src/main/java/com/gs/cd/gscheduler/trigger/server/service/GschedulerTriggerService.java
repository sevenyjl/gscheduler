package com.gs.cd.gscheduler.trigger.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.gscheduler.trigger.server.entity.GschedulerTrigger;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
public interface GschedulerTriggerService extends IService<GschedulerTrigger> {


    boolean create(GschedulerTrigger gschedulerTrigger);

    GschedulerTrigger getByTaskIdAndGroupName(String tenantCode, String taskId, String groupName);

    boolean delete(String tenantCode, String taskId, String groupName);

    boolean edit(GschedulerTrigger gschedulerTrigger);

    boolean lockById(GschedulerTrigger gschedulerTrigger);

    boolean lockBathById(List<GschedulerTrigger> gschedulerTriggerList);

    boolean unlockById(GschedulerTrigger gschedulerTrigger);

    boolean unlockBathById(List<GschedulerTrigger> gschedulerTriggerList);

}

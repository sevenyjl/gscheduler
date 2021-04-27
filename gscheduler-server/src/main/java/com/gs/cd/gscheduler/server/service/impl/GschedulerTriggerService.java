package com.gs.cd.gscheduler.server.service.impl;

import com.gs.cd.gscheduler.dao.entity.GschedulerTrigger;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
public interface GschedulerTriggerService extends IService<GschedulerTrigger> {

    /**
     * 通过业务id 和 groupName 查询
     *
     * @param taskId
     * @param groupName
     * @return
     */
    GschedulerTrigger getByTaskIdAndGroupName(String taskId, String groupName);
}

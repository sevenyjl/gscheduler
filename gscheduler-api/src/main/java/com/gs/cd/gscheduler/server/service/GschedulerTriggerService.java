package com.gs.cd.gscheduler.server.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;

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

    /**
     * 通过业务id 和 groupName 查询
     *
     * @param taskId
     * @param groupName
     * @return
     */
    GschedulerTrigger getByTaskIdAndGroupName(String taskId, String groupName);

    // TODO: 2021/4/28 可能会存在 租户在其他pg表上情况
    List<String> listAllTenantCode();

    List<GschedulerTrigger> listByTenantCode(String tenantCode);
}

package com.gs.cd.gscheduler.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.gscheduler.common.entity.Schedule;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 
 * @since 2021-04-25
 */
public interface SchedulesService extends IService<Schedule> {

    List<Schedule> selectAllByProcessDefineArray(int[] ints);

    List<Schedule> queryByProcessDefinitionId(Integer processDefinitionId);
}
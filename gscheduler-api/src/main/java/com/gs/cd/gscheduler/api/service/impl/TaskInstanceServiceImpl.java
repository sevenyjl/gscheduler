package com.gs.cd.gscheduler.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.gscheduler.api.service.SchedulesService;
import com.gs.cd.gscheduler.api.service.TaskInstanceService;
import com.gs.cd.gscheduler.common.entity.Schedule;
import com.gs.cd.gscheduler.common.entity.TaskInstance;
import com.gs.cd.gscheduler.common.enums.Flag;
import com.gs.cd.gscheduler.dao.mapper.ScheduleMapper;
import com.gs.cd.gscheduler.dao.mapper.TaskInstanceMapper;
import com.gs.cd.gscheduler.quartz.QuartzExecutors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2021-04-25
 */
@Service
@Log4j2
public class TaskInstanceServiceImpl extends ServiceImpl<TaskInstanceMapper, TaskInstance> implements TaskInstanceService {

    @Override
    public List<TaskInstance> findValidTaskListByProcessId(Integer processInstanceId) {
        return list(new QueryWrapper<TaskInstance>().lambda()
                .eq(TaskInstance::getFlag, Flag.NO)
                .eq(TaskInstance::getProcessInstanceId, processInstanceId));
    }
}

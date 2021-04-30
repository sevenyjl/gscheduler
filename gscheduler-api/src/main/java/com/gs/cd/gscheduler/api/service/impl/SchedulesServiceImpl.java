package com.gs.cd.gscheduler.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.gscheduler.api.service.SchedulesService;
import com.gs.cd.gscheduler.common.entity.Schedule;
import com.gs.cd.gscheduler.dao.mapper.ScheduleMapper;
import com.gs.cd.gscheduler.quartz.QuartzExecutors;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
public class SchedulesServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements SchedulesService {
    @Autowired
    QuartzExecutors quartzExecutors;

    @Override
    public boolean deleteSchedule(String tenantCode, int projectId, int scheduleId) throws RuntimeException {
        log.info("delete schedules of project id:{}, schedule id:{}", projectId, scheduleId);
        boolean b = quartzExecutors.deleteJob(tenantCode, String.valueOf(scheduleId), String.valueOf(projectId));
        boolean b1 = removeById(scheduleId);
        return b & b1;
    }

    @Override
    public List<Schedule> selectAllByProcessDefineArray(int[] ints) {
        return null;
    }

    @Override
    public List<Schedule> queryByProcessDefinitionId(Integer processDefinitionId) {
        return list(new QueryWrapper<Schedule>().lambda().eq(Schedule::getProcessDefinitionId, processDefinitionId));
    }

    @Override
    public void deleteByDefinitionId(String tenantCode, Integer projectId, Integer processDefinitionId) {
        List<Schedule> schedules = listByDefinitionId(processDefinitionId);
        schedules.forEach(schedule -> deleteSchedule(tenantCode, projectId, schedule.getId()));
    }

    public List<Schedule> listByDefinitionId(Integer processDefinitionId) {
        return list(new QueryWrapper<Schedule>().lambda().eq(Schedule::getProcessDefinitionId, processDefinitionId));
    }

}

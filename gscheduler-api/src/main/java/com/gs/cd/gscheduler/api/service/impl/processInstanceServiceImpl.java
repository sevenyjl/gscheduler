package com.gs.cd.gscheduler.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.service.ProcessInstanceService;
import com.gs.cd.gscheduler.api.service.ProjectService;
import com.gs.cd.gscheduler.api.service.TaskInstanceService;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.ProcessInstance;
import com.gs.cd.gscheduler.common.entity.Project;
import com.gs.cd.gscheduler.common.entity.TaskInstance;
import com.gs.cd.gscheduler.dao.mapper.ProcessInstanceMapper;
import com.gs.cd.gscheduler.dao.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gs.cd.gscheduler.common.Constants.PROCESS_INSTANCE_STATE;
import static com.gs.cd.gscheduler.common.Constants.TASK_LIST;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2021-04-23
 */
@Service
public class processInstanceServiceImpl extends ServiceImpl<ProcessInstanceMapper, ProcessInstance> implements ProcessInstanceService {

    @Autowired
    TaskInstanceService taskInstanceService;

    @Override
    public Map<String, Object> queryTaskListByProcessId(String projectName, Integer processInstanceId) {
        ProcessInstance processInstance = getById(processInstanceId);
        List<TaskInstance> taskInstanceList = taskInstanceService.findValidTaskListByProcessId(processInstanceId);
//        addDependResultForTaskList(taskInstanceList);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put(PROCESS_INSTANCE_STATE, processInstance.getState().toString());
        resultMap.put(TASK_LIST, taskInstanceList);
        return resultMap;
    }
}

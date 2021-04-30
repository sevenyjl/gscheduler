package com.gs.cd.gscheduler.api.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.service.ProcessDefinitionService;
import com.gs.cd.gscheduler.api.service.ProcessInstanceService;
import com.gs.cd.gscheduler.api.service.ProjectService;
import com.gs.cd.gscheduler.api.service.TaskInstanceService;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.*;
import com.gs.cd.gscheduler.common.enums.Flag;
import com.gs.cd.gscheduler.common.process.Property;
import com.gs.cd.gscheduler.common.utils.ParameterUtils;
import com.gs.cd.gscheduler.dao.mapper.ProcessInstanceMapper;
import com.gs.cd.gscheduler.dao.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    @Autowired
    ProjectService projectService;
    @Autowired
    ProcessDefinitionService processDefinitionService;

    @Override
    public ProcessInstance getById(Serializable id) {
        ProcessInstance processInstance = super.getById(id);
        ProcessDefinition processDefinition = processDefinitionService.getById(processInstance.getProcessDefinitionId());
        processInstance.setReceiversCc(processDefinition.getReceiversCc());
        return processInstance;
    }

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

    @Override
    public boolean updateProcessInstance(JwtUserInfo loginUser, String projectName, Integer processInstanceId,
                                         String processInstanceJson, String scheduleTime, Boolean syncDefine, Flag flag,
                                         String locations, String connects) {
        Project project = projectService.queryByName(projectName);
        if (project == null) {
            throw new RuntimeException(String.format("不存在名称=%s的项目", projectName));
        }
        ProcessInstance processInstance = getById(processInstanceId);
        if (processInstance == null) {
            throw new RuntimeException(String.format("不存在id=%s的工作流定义", processInstanceId));
        }

        Date schedule = null;
        if (scheduleTime != null) {
            schedule = DateUtil.parse(scheduleTime, Constants.YYYY_MM_DD_HH_MM_SS);
        } else {
            schedule = processInstance.getScheduleTime();
        }
        processInstance.setScheduleTime(schedule);
        processInstance.setLocations(locations);
        processInstance.setConnects(connects);
        String globalParams = null;
        String originDefParams = null;
        int timeout = processInstance.getTimeout();
        ProcessDefinition processDefinition = processDefinitionService.getById(processInstance.getProcessDefinitionId());
        if (StrUtil.isNotEmpty(processInstanceJson)) {
            ProcessData processData = JSONUtil.toBean(processInstanceJson, ProcessData.class);
            //check workflow json is valid
            processDefinitionService.checkProcessNodeList(processData, processInstanceJson);

            originDefParams = JSONUtil.toJsonStr(processData.getGlobalParams());
            List<Property> globalParamList = processData.getGlobalParams();
            Map<String, String> globalParamMap = globalParamList.stream().collect(Collectors.toMap(Property::getProp, Property::getValue));
            globalParams = ParameterUtils.curingGlobalParams(globalParamMap, globalParamList,
                    processInstance.getCmdTypeIfComplement(), schedule);
            timeout = processData.getTimeout();
            processInstance.setTimeout(timeout);
//            Tenant tenant = processService.getTenantForProcess(processData.getTenantId(),
//                    processDefinition.getUserId());
//            if (tenant != null) {
//                processInstance.setTenantCode(tenant.getTenantCode());
//            }
            // TODO: 2021/4/30 设置租户code / id
            processInstance.setProcessInstanceJson(processInstanceJson);
            processInstance.setGlobalParams(globalParams);
        }
        boolean update = updateById(processInstance);
        boolean updateDefine = false;
        if (Boolean.TRUE.equals(syncDefine) && StrUtil.isNotEmpty(processInstanceJson)) {
            processDefinition.setProcessDefinitionJson(processInstanceJson);
            processDefinition.setGlobalParams(originDefParams);
            processDefinition.setLocations(locations);
            processDefinition.setConnects(connects);
            processDefinition.setTimeout(timeout);
            updateDefine = processDefinitionService.updateById(processDefinition);
        }
        return update && updateDefine;
    }

    @Override
    public boolean removeById(Serializable id) {

        return super.removeById(id);
    }
}

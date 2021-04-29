package com.gs.cd.gscheduler.api.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.utils.DateUtils;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.dto.ProcessMeta;
import com.gs.cd.gscheduler.api.service.ProcessDefinitionService;
import com.gs.cd.gscheduler.api.service.ProjectService;
import com.gs.cd.gscheduler.api.service.SchedulesService;
import com.gs.cd.gscheduler.api.utils.CheckUtils;
import com.gs.cd.gscheduler.api.utils.exportprocess.ProcessAddTaskParam;
import com.gs.cd.gscheduler.api.utils.exportprocess.TaskNodeParamFactory;
import com.gs.cd.gscheduler.common.entity.ProcessData;
import com.gs.cd.gscheduler.common.entity.ProcessDefinition;
import com.gs.cd.gscheduler.common.entity.Project;
import com.gs.cd.gscheduler.common.entity.Schedule;
import com.gs.cd.gscheduler.common.enums.Flag;
import com.gs.cd.gscheduler.common.enums.ReleaseState;
import com.gs.cd.gscheduler.common.graph.DAG;
import com.gs.cd.gscheduler.common.model.TaskNode;
import com.gs.cd.gscheduler.common.process.Property;
import com.gs.cd.gscheduler.common.process.ResourceInfo;
import com.gs.cd.gscheduler.common.task.AbstractParameters;
import com.gs.cd.gscheduler.common.utils.TaskParametersUtils;
import com.gs.cd.gscheduler.dao.mapper.ProcessDefinitionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.security.PermissionCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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
public class ProcessDefinitionServiceImpl extends ServiceImpl<ProcessDefinitionMapper, ProcessDefinition> implements ProcessDefinitionService {

    @Autowired
    ProjectService projectService;
    @Autowired
    SchedulesService schedulesService;

    @Override
    public boolean createProcessDefinition(JwtUserInfo loginUser, String projectName, String name, String json, String description, String locations, String connects) {
        Project project = projectService.queryByName(projectName);
        if (project == null) {
            throw new RuntimeException(String.format("不存在名称=%s的项目", projectName));
        }
        ProcessDefinition processDefinition = new ProcessDefinition();
        ProcessData processData = JSONUtil.toBean(json, ProcessData.class);
        checkProcessNodeList(processData, json);

        processDefinition.setName(name);
        processDefinition.setReleaseState(ReleaseState.OFFLINE);
        processDefinition.setProjectId(project.getId());
        processDefinition.setUserId(loginUser.getUserId().toString());
        processDefinition.setProcessDefinitionJson(json);
        processDefinition.setDescription(description);
        processDefinition.setLocations(locations);
        processDefinition.setConnects(connects);
        processDefinition.setTimeout(processData.getTimeout());
        processDefinition.setTenantId(processData.getTenantId());
        processDefinition.setModifyBy(loginUser.getUserName());
        processDefinition.setResourceIds(getResourceIds(processData));

        //custom global params
        List<Property> globalParamsList = processData.getGlobalParams();
        if (CollectionUtil.isNotEmpty(globalParamsList)) {
            Set<Property> globalParamsSet = new HashSet<>(globalParamsList);
            globalParamsList = new ArrayList<>(globalParamsSet);
            processDefinition.setGlobalParamList(globalParamsList);
        }
        processDefinition.setCreateTime(new Date());
        processDefinition.setUpdateTime(new Date());
        processDefinition.setCreator(loginUser.getUserName());
        processDefinition.setUpdater(loginUser.getUserName());
        processDefinition.setFlag(Flag.YES);
        return save(processDefinition);
    }

    @Override
    public boolean copyProcessDefinition(JwtUserInfo loginUser, String projectName, int processId) {
        Project project = projectService.queryByName(projectName);
        if (project == null) {
            throw new RuntimeException(String.format("不存在名称=%s的项目", projectName));
        }
        ProcessDefinition processDefinition = getById(processId);
        if (processDefinition == null) {
            throw new RuntimeException(String.format("不存在id=%s的工作流定义", processId));
        } else {
            return createProcessDefinition(
                    loginUser,
                    projectName,
                    processDefinition.getName() + "_copy_" + System.currentTimeMillis(),
                    processDefinition.getProcessDefinitionJson(),
                    processDefinition.getDescription(),
                    processDefinition.getLocations(),
                    processDefinition.getConnects());
        }
    }

    @Override
    public ApiResult verifyProcessDefinitionName(JwtUserInfo loginUser, String projectName, String name) {
        Project project = projectService.queryByName(projectName);
        if (project == null) {
            throw new RuntimeException(String.format("不存在名称=%s的项目", projectName));
        }
        ProcessDefinition processDefinition = getOne(new QueryWrapper<ProcessDefinition>().lambda()
                .eq(ProcessDefinition::getName, name).eq(ProcessDefinition::getProjectId, project.getId()));
        if (processDefinition == null) {
            return ApiResult.success();
        } else {
            return ApiResult.error(String.format("存在名称=%s的工作流定义", name));
        }
    }

    @Override
    public boolean updateProcessDefinition(JwtUserInfo loginUser, String projectName, int id, String name,
                                           String processDefinitionJson, String description, String locations, String connects) {
        Project project = projectService.queryByName(projectName);
        if (project == null) {
            throw new RuntimeException(String.format("不存在名称=%s的项目", projectName));
        }

        ProcessData processData = JSONUtil.toBean(processDefinitionJson, ProcessData.class);
        ProcessDefinition processDefinition = getById(id);
        // check process definition exists
        if (processDefinition == null) {
            throw new RuntimeException(String.format("不存在id=%s的工作流定义", id));
        }
        if (processDefinition.getReleaseState() == ReleaseState.ONLINE) {
            // online can not permit edit
            throw new RuntimeException("工作流未下线，不能进行修改");
        }

        if (!name.equals(processDefinition.getName())) {
            // check whether the new process define name exist
            ApiResult apiResult = verifyProcessDefinitionName(loginUser, projectName, name);
            if (!apiResult.isSuccess()) {
                throw new RuntimeException(apiResult.getMsg());
            }
        }
        processDefinition.setId(id);
        processDefinition.setName(name);
        processDefinition.setReleaseState(ReleaseState.OFFLINE);
        processDefinition.setProjectId(project.getId());
        processDefinition.setProcessDefinitionJson(processDefinitionJson);
        processDefinition.setDescription(description);
        processDefinition.setLocations(locations);
        processDefinition.setConnects(connects);
        processDefinition.setTimeout(processData.getTimeout());
        processDefinition.setTenantId(processData.getTenantId());
        processDefinition.setModifyBy(loginUser.getUserName());
        processDefinition.setResourceIds(getResourceIds(processData));
        //custom global params
        List<Property> globalParamsList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(processData.getGlobalParams())) {
            Set<Property> userDefParamsSet = new HashSet<>(processData.getGlobalParams());
            globalParamsList = new ArrayList<>(userDefParamsSet);
        }
        processDefinition.setGlobalParamList(globalParamsList);
        processDefinition.setUpdateTime(new Date());
        processDefinition.setUpdater(loginUser.getUserName());
        processDefinition.setFlag(Flag.YES);
        return updateById(processDefinition);
    }

    @Override
    public boolean releaseProcessDefinition(JwtUserInfo loginUser, String projectName, int processId, int releaseState) {
        return false;
    }

    @Override
    public List<ProcessDefinition> listByProjectName(String projectName) {
        return list(new QueryWrapper<ProcessDefinition>().lambda().eq(ProcessDefinition::getProjectName, projectName));
    }

    @Override
    public List<TaskNode> getTaskNodeListByDefinitionId(Integer processDefinitionId) {
        ProcessDefinition processDefinition = getById(processDefinitionId);
        if (processDefinition == null) {
            throw new RuntimeException(String.format("不存在id=%s的工作流定义", processDefinitionId));
        }

        String processDefinitionJson = processDefinition.getProcessDefinitionJson();

        ProcessData processData = JSONUtil.toBean(processDefinitionJson, ProcessData.class);

        //process data check
        if (null == processData) {
            log.error("process data is null");
            throw new RuntimeException("process data is null");
        }
        return (processData.getTasks() == null) ? new ArrayList<>() : processData.getTasks();
    }

    @Override
    public Map<Integer, List<TaskNode>> getTaskNodeListByDefinitionIdList(String processDefinitionIdList) {
        String[] idList = processDefinitionIdList.split(",");
        List<Integer> idIntList = new ArrayList<>();
        for (String definitionId : idList) {
            idIntList.add(Integer.parseInt(definitionId.trim()));
        }
        Map<Integer, List<TaskNode>> taskNodeMap = new HashMap<>();
        Collection<ProcessDefinition> processDefinitions = listByIds(idIntList);
        processDefinitions.forEach(s -> {
            String processDefinitionJson = s.getProcessDefinitionJson();
            ProcessData processData = JSONUtil.toBean(processDefinitionJson, ProcessData.class);
            List<TaskNode> taskNodeList = (processData.getTasks() == null) ? new ArrayList<>() : processData.getTasks();
            taskNodeMap.put(s.getId(), taskNodeList);
        });
        return taskNodeMap;
    }

    @Override
    public void batchExportProcessDefinitionByIds(String projectName, String processDefinitionIds, HttpServletResponse response) {

        if (StrUtil.isEmpty(processDefinitionIds)) {
            return;
        }

        List<ProcessMeta> processDefinitionList =
                getProcessDefinitionList(processDefinitionIds);

        if (CollectionUtil.isNotEmpty(processDefinitionList)) {
            downloadProcessDefinitionFile(response, processDefinitionList);
        }
    }

    private void downloadProcessDefinitionFile(HttpServletResponse response, List<ProcessMeta> processDefinitionList) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        BufferedOutputStream buff = null;
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            buff = new BufferedOutputStream(out);
            buff.write(JSON.toJSONString(processDefinitionList).getBytes(StandardCharsets.UTF_8));
            buff.flush();
            buff.close();
        } catch (IOException e) {
            log.warn("export process fail", e);
        } finally {
            if (null != buff) {
                try {
                    buff.close();
                } catch (Exception e) {
                    log.warn("export process buffer not close", e);
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (Exception e) {
                    log.warn("export process output stream not close", e);
                }
            }
        }
    }


    private List<ProcessMeta> getProcessDefinitionList(String processDefinitionIds) {
        List<ProcessMeta> processDefinitionList = new ArrayList<>();
        String[] processDefinitionIdArray = processDefinitionIds.split(",");
        for (String strProcessDefinitionId : processDefinitionIdArray) {
            //get workflow info
            int processDefinitionId = Integer.parseInt(strProcessDefinitionId);
            ProcessDefinition processDefinition = getById(processDefinitionId);
            if (null != processDefinition) {
                processDefinitionList.add(exportProcessMetaData(processDefinitionId, processDefinition));
            }
        }

        return processDefinitionList;
    }

    public ProcessMeta exportProcessMetaData(Integer processDefinitionId, ProcessDefinition processDefinition) {
        //correct task param which has data source or dependent param
        String correctProcessDefinitionJson = addExportTaskNodeSpecialParam(processDefinition.getProcessDefinitionJson());
        processDefinition.setProcessDefinitionJson(correctProcessDefinitionJson);

        //export process metadata
        ProcessMeta exportProcessMeta = new ProcessMeta();
        exportProcessMeta.setProjectName(processDefinition.getProjectName());
        exportProcessMeta.setProcessDefinitionName(processDefinition.getName());
        exportProcessMeta.setProcessDefinitionJson(processDefinition.getProcessDefinitionJson());
        exportProcessMeta.setProcessDefinitionLocations(processDefinition.getLocations());
        exportProcessMeta.setProcessDefinitionConnects(processDefinition.getConnects());

        //schedule info
        List<Schedule> schedules = schedulesService.queryByProcessDefinitionId(processDefinitionId);
        if (!schedules.isEmpty()) {
            Schedule schedule = schedules.get(0);
            exportProcessMeta.setScheduleWarningType(schedule.getWarningType().toString());
            exportProcessMeta.setScheduleWarningGroupId(schedule.getWarningGroupId());
            exportProcessMeta.setScheduleStartTime(DateUtils.dateTime(schedule.getStartTime()));
            exportProcessMeta.setScheduleEndTime(DateUtils.dateTime(schedule.getEndTime()));
            exportProcessMeta.setScheduleCrontab(schedule.getCrontab());
            exportProcessMeta.setScheduleFailureStrategy(String.valueOf(schedule.getFailureStrategy()));
            exportProcessMeta.setScheduleReleaseState(String.valueOf(ReleaseState.OFFLINE));
            exportProcessMeta.setScheduleProcessInstancePriority(String.valueOf(schedule.getProcessInstancePriority()));
            exportProcessMeta.setScheduleWorkerGroupName(schedule.getWorkerGroup());
        }
        //create workflow json file
        return exportProcessMeta;
    }

    /**
     * correct task param which has datasource or dependent
     *
     * @param processDefinitionJson processDefinitionJson
     * @return correct processDefinitionJson
     */
    public String addExportTaskNodeSpecialParam(String processDefinitionJson) {
        JSONObject jsonObject = JSONUtil.parseObj(processDefinitionJson);
        JSONArray jsonArray = jsonObject.getJSONArray("tasks");
        jsonArray.forEach(j -> {
            JSONObject taskNode = (JSONObject) j;
            if (StrUtil.isNotEmpty(taskNode.getStr("type"))) {
                String taskType = taskNode.getStr("type");
                ProcessAddTaskParam addTaskParam = TaskNodeParamFactory.getByTaskType(taskType);
                if (null != addTaskParam) {
                    addTaskParam.addExportSpecialParam(taskNode);
                }
            }
        });
        jsonObject.put("tasks", jsonArray);
        return jsonObject.toString();
    }


    /**
     * get resource ids
     *
     * @param processData process data
     * @return resource ids
     */
    private String getResourceIds(ProcessData processData) {
        List<TaskNode> tasks = processData.getTasks();
        Set<Integer> resourceIds = new HashSet<>();
        for (TaskNode taskNode : tasks) {
            String taskParameter = taskNode.getParams();
            AbstractParameters params = TaskParametersUtils.getParameters(taskNode.getType(), taskParameter);
            if (CollectionUtil.isNotEmpty(params.getResourceFilesList())) {
                Set<Integer> tempSet = params.getResourceFilesList().stream().filter(t -> t.getId() != 0).map(ResourceInfo::getId).collect(Collectors.toSet());
                resourceIds.addAll(tempSet);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i : resourceIds) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(i);
        }
        return sb.toString();
    }

    public void checkProcessNodeList(ProcessData processData, String processDefinitionJson) {

        try {
            if (processData == null) {
                log.error("process data is null");
                throw new RuntimeException(String.format("请求参数[%s]无效", processDefinitionJson));
            }
            List<TaskNode> taskNodes = processData.getTasks();

            if (taskNodes == null) {
                log.error("process node info is empty");
                throw new RuntimeException(String.format("数据[%s]不能为空", processDefinitionJson));
            }

            // check has cycle
            if (graphHasCycle(taskNodes)) {
                log.error("process DAG has cycle processDefinitionJson=" + processDefinitionJson);
                throw new RuntimeException("流程节点间存在循环依赖");
            }

            // check whether the process definition json is normal
            for (TaskNode taskNode : taskNodes) {
                if (!CheckUtils.checkTaskNodeParameters(taskNode.getParams(), taskNode.getType())) {
                    log.error("task node {} parameter invalid", taskNode.getName());
                    throw new RuntimeException("任务节点参数无效，节点名称=" + taskNode.getName());
                }
                // check extra params
                CheckUtils.checkOtherParams(taskNode.getExtras());
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("检测任务节点错误");
        }
    }

    private boolean graphHasCycle(List<TaskNode> taskNodeResponseList) {
        DAG<String, TaskNode, String> graph = new DAG<>();

        // Fill the vertices
        for (TaskNode taskNodeResponse : taskNodeResponseList) {
            graph.addNode(taskNodeResponse.getName(), taskNodeResponse);
        }

        // Fill edge relations
        for (TaskNode taskNodeResponse : taskNodeResponseList) {
            taskNodeResponse.getPreTasks();
            List<String> preTasks = JSONUtil.toList(taskNodeResponse.getPreTasks(), String.class);
            if (CollectionUtil.isNotEmpty(preTasks)) {
                for (String preTask : preTasks) {
                    if (!graph.addEdge(preTask, taskNodeResponse.getName())) {
                        return true;
                    }
                }
            }
        }
        return graph.hasCycle();
    }
}

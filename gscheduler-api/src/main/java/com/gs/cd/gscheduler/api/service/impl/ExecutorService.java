package com.gs.cd.gscheduler.api.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.utils.DateUtils;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.enums.ExecuteType;
import com.gs.cd.gscheduler.api.service.*;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.*;
import com.gs.cd.gscheduler.common.enums.*;
import com.gs.cd.gscheduler.quartz.utils.CronUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.util.*;

import static com.gs.cd.gscheduler.common.Constants.*;

/**
 * executor service
 */
@Service
@Slf4j
public class ExecutorService {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProcessDefinitionService processDefinitionService;
    @Autowired
    private ProcessInstanceService processInstanceService;
    @Autowired
    private ProcessService processService;
    @Autowired
    private CommandService commandService;
    @Autowired
    private SchedulesService schedulesService;

    public void execProcessInstance(JwtUserInfo loginUser, String projectName,
                                    int processDefinitionId, String cronTime, CommandType commandType,
                                    FailureStrategy failureStrategy, String startNodeList,
                                    TaskDependType taskDependType, WarningType warningType, int warningGroupId,
                                    String receivers, String receiversCc, RunMode runMode,
                                    Priority processInstancePriority, String workerGroup, Integer timeout) throws Exception {
        // timeout is invalid
        if (timeout <= 0 || timeout > Constants.MAX_TASK_TIMEOUT) {
            throw new RuntimeException("任务超时参数无效");
        }
        Project project = projectService.queryByName(projectName);
        if (project == null) {
            throw new RuntimeException(String.format("不存在名称为%s的项目", projectName));
        }
        ProcessDefinition processDefinition = processDefinitionService.getById(processDefinitionId);
        // check process define release state
        checkProcessDefinitionValid(processDefinition, processDefinitionId);
        /**
         * create command
         */
        boolean create = this.createCommand(commandType, processDefinitionId,
                taskDependType, failureStrategy, startNodeList, cronTime, warningType, loginUser.getUserId().toString(),
                warningGroupId, runMode, processInstancePriority, workerGroup);
        if (create) {
            /**
             * according to the process definition ID updateProcessInstance and CC recipient
             */
            processDefinition.setReceivers(receivers);
            processDefinition.setReceiversCc(receiversCc);
            processDefinitionService.updateById(processDefinition);
        } else {
            throw new RuntimeException("运行工作流实例错误");
        }
    }


    /**
     * check whether the process definition can be executed
     *
     * @param processDefinition process definition
     * @param processDefineId   process definition id
     * @return check result code
     */
    public void checkProcessDefinitionValid(ProcessDefinition processDefinition, int processDefineId) {
        if (processDefinition == null) {
            throw new RuntimeException(String.format("不存在id=%s工作流定义", processDefineId));
        } else if (processDefinition.getReleaseState() != ReleaseState.ONLINE) {
            // check process definition online
            throw new RuntimeException(String.format("工作流名称为%s的工作流定义未上线", processDefinition.getName()));
        }
    }


    /**
     * do action to process instance：pause, stop, repeat, recover from pause, recover from stop
     *
     * @param loginUser         login user
     * @param projectName       project name
     * @param processInstanceId process instance id
     * @param executeType       execute type
     * @return execute result code
     */
    public void execute(JwtUserInfo loginUser, String projectName, Integer processInstanceId, ExecuteType executeType) {
        Project project = projectService.queryByName(projectName);
        ProcessInstance processInstance = processInstanceService.getById(processInstanceId);
        if (processInstance == null) {
            throw new RuntimeException(String.format("工作流实例id为%s的数据不存在", processInstanceId));
        }

        ProcessDefinition processDefinition = processDefinitionService.getById(processInstance.getProcessDefinitionId());
        if (executeType != ExecuteType.STOP && executeType != ExecuteType.PAUSE) {
            checkProcessDefinitionValid(processDefinition, processInstance.getProcessDefinitionId());
        }
        checkExecuteType(processInstance, executeType);

        switch (executeType) {
            case REPEAT_RUNNING:
                insertCommand(loginUser, processInstanceId, processDefinition.getId(), CommandType.REPEAT_RUNNING);
                break;
            case RECOVER_SUSPENDED_PROCESS:
                insertCommand(loginUser, processInstanceId, processDefinition.getId(), CommandType.RECOVER_SUSPENDED_PROCESS);
                break;
            case START_FAILURE_TASK_PROCESS:
                insertCommand(loginUser, processInstanceId, processDefinition.getId(), CommandType.START_FAILURE_TASK_PROCESS);
                break;
            case STOP:
                if (processInstance.getState() == ExecutionStatus.READY_STOP) {
                    throw new RuntimeException(String.format("工作流实例[%s]的状态已经是[%s]", processInstance.getName(), processInstance.getState()));
                } else {
                    updateProcessInstancePrepare(processInstance, CommandType.STOP, ExecutionStatus.READY_STOP);
                }
                break;
            case PAUSE:
                if (processInstance.getState() == ExecutionStatus.READY_PAUSE) {
                    throw new RuntimeException(String.format("工作流实例[%s]的状态已经是[%s]", processInstance.getName(), processInstance.getState()));
                } else {
                    updateProcessInstancePrepare(processInstance, CommandType.PAUSE, ExecutionStatus.READY_PAUSE);
                }
                break;
            default:
                log.error("unknown execute type : {}", executeType);
                throw new RuntimeException("unknown execute type");
        }
    }

    /**
     * Check the state of process instance and the type of operation match
     *
     * @param processInstance process instance
     * @param executeType     execute type
     * @return check result code
     */
    private void checkExecuteType(ProcessInstance processInstance, ExecuteType executeType) {

        Map<String, Object> result = new HashMap<>(5);
        ExecutionStatus executionStatus = processInstance.getState();
        boolean checkResult = false;
        switch (executeType) {
            case PAUSE:
            case STOP:
                if (executionStatus.typeIsRunning()) {
                    checkResult = true;
                }
                break;
            case REPEAT_RUNNING:
                if (executionStatus.typeIsFinished()) {
                    checkResult = true;
                }
                break;
            case START_FAILURE_TASK_PROCESS:
                if (executionStatus.typeIsFailure()) {
                    checkResult = true;
                }
                break;
            case RECOVER_SUSPENDED_PROCESS:
                if (executionStatus.typeIsPause() || executionStatus.typeIsCancel()) {
                    checkResult = true;
                }
                break;
            default:
                break;
        }
        if (!checkResult) {
            throw new RuntimeException(String.format("工作流实例[%s]的状态是[%s]，无法执行[%s]操作", processInstance.getName(), executionStatus.toString(), executeType.toString()));
        }
    }

    /**
     * prepare to update process instance command type and status
     *
     * @param processInstance process instance
     * @param commandType     command type
     * @param executionStatus execute status
     * @return update result
     */
    private boolean updateProcessInstancePrepare(ProcessInstance processInstance, CommandType commandType, ExecutionStatus executionStatus) {
        Map<String, Object> result = new HashMap<>(5);

        processInstance.setCommandType(commandType);
        processInstance.addHistoryCmd(commandType);
        processInstance.setState(executionStatus);
        return processInstanceService.updateById(processInstance);
    }

    /**
     * insert command, used in the implementation of the page, re run, recovery (pause / failure) execution
     *
     * @param loginUser           login user
     * @param instanceId          instance id
     * @param processDefinitionId process definition id
     * @param commandType         command type
     * @return insert result code
     */
    private boolean insertCommand(JwtUserInfo loginUser, Integer instanceId, Integer processDefinitionId, CommandType commandType) {
        Map<String, Object> result = new HashMap<>(5);
        Command command = new Command();
        command.setCommandType(commandType);
        command.setProcessDefinitionId(processDefinitionId);
        command.setCommandParam(String.format("{\"%s\":%d}",
                CMDPARAM_RECOVER_PROCESS_ID_STRING, instanceId));
        command.setExecutorId(loginUser.getUserId().toString());
        if (!commandService.verifyIsNeedCreateCommand(command)) {
            throw new RuntimeException(String.format("工作流实例[%s]正在执行命令，请稍等...", processDefinitionId));
        }
        return commandService.save(command);
    }

    /**
     * check if sub processes are offline before starting process definition
     *
     * @param processDefineId process definition id
     * @return check result code
     */
    public void startCheckByProcessDefinedId(int processDefineId) {
        List<Integer> ids = new ArrayList<>();
        processDefinitionService.recurseFindSubProcessId(processDefineId, ids);
        Integer[] idArray = ids.toArray(new Integer[ids.size()]);
        if (!ids.isEmpty()) {
            Collection<ProcessDefinition> processDefinitionList = processDefinitionService.listByIds(Arrays.asList(idArray.clone()));
            if (processDefinitionList != null) {
                for (ProcessDefinition processDefinition : processDefinitionList) {
                    /**
                     * if there is no online process, exit directly
                     */
                    if (processDefinition.getReleaseState() != ReleaseState.ONLINE) {
                        log.info("not release process definition id: {} , name : {}",
                                processDefinition.getId(), processDefinition.getName());
                        throw new RuntimeException(String.format("工作流定义[%s]不是上线状态", processDefinition.getName()));
                    }
                }
            }
        }
    }

    /**
     * query recipients and copyers by process definition id or processInstanceId
     *
     * @param processDefineId   process definition id
     * @param processInstanceId process instance id
     * @return receivers cc list
     */
//    public Map<String, Object> getReceiverCc(Integer processDefineId, Integer processInstanceId) {
//        Map<String, Object> result = new HashMap<>();
//        log.info("processInstanceId {}", processInstanceId);
//        if (processDefineId == null && processInstanceId == null) {
//            throw new RuntimeException("You must set values for parameters processDefineId or processInstanceId");
//        }
//        if (processDefineId == null && processInstanceId != null) {
//            ProcessInstance processInstance = processInstanceMapper.selectById(processInstanceId);
//            if (processInstance == null) {
//                throw new RuntimeException("processInstanceId is not exists");
//            }
//            processDefineId = processInstance.getProcessDefinitionId();
//        }
//        ProcessDefinition processDefinition = processDefinitionMapper.selectById(processDefineId);
//        if (processDefinition == null) {
//            throw new RuntimeException(String.format("processDefineId %d is not exists", processDefineId));
//        }
//
//        String receivers = processDefinition.getReceivers();
//        String receiversCc = processDefinition.getReceiversCc();
//        Map<String, String> dataMap = new HashMap<>();
//        dataMap.put(Constants.RECEIVERS, receivers);
//        dataMap.put(Constants.RECEIVERS_CC, receiversCc);
//
//        result.put(Constants.DATA_LIST, dataMap);
//        putMsg(result, Status.SUCCESS);
//        return result;
//    }


    /**
     * create command
     *
     * @param commandType             commandType
     * @param processDefineId         processDefineId
     * @param nodeDep                 nodeDep
     * @param failureStrategy         failureStrategy
     * @param startNodeList           startNodeList
     * @param schedule                schedule
     * @param warningType             warningType
     * @param executorId              executorId
     * @param warningGroupId          warningGroupId
     * @param runMode                 runMode
     * @param processInstancePriority processInstancePriority
     * @param workerGroup             workerGroup
     * @return command id
     * @throws Exception
     */
    private boolean createCommand(CommandType commandType, int processDefineId,
                                  TaskDependType nodeDep, FailureStrategy failureStrategy,
                                  String startNodeList, String schedule, WarningType warningType,
                                  String executorId, int warningGroupId,
                                  RunMode runMode, Priority processInstancePriority, String workerGroup) throws Exception {

        /**
         * instantiate command schedule instance
         */
        Command command = new Command();

        Map<String, String> cmdParam = new HashMap<>();
        if (commandType == null) {
            command.setCommandType(CommandType.START_PROCESS);
        } else {
            command.setCommandType(commandType);
        }
        command.setProcessDefinitionId(processDefineId);
        if (nodeDep != null) {
            command.setTaskDependType(nodeDep);
        }
        if (failureStrategy != null) {
            command.setFailureStrategy(failureStrategy);
        }

        if (StrUtil.isNotEmpty(startNodeList)) {
            cmdParam.put(CMDPARAM_START_NODE_NAMES, startNodeList);
        }
        if (warningType != null) {
            command.setWarningType(warningType);
        }
        command.setCommandParam(JSONUtil.toJsonStr(cmdParam));
        command.setExecutorId(executorId);
        command.setWarningGroupId(warningGroupId);
        command.setProcessInstancePriority(processInstancePriority);
        command.setWorkerGroup(workerGroup);

        Date start = null;
        Date end = null;
        if (StrUtil.isNotEmpty(schedule)) {
            String[] interval = schedule.split(",");
            if (interval.length == 2) {
                start = DateUtil.parse(interval[0], Constants.YYYY_MM_DD_HH_MM_SS);
                end = DateUtil.parse(interval[1], Constants.YYYY_MM_DD_HH_MM_SS);
            }
        }

        // determine whether to complement
        if (commandType == CommandType.COMPLEMENT_DATA) {
            runMode = (runMode == null) ? RunMode.RUN_MODE_SERIAL : runMode;
            if (null != start && null != end && !start.after(end)) {
                if (runMode == RunMode.RUN_MODE_SERIAL) {
                    cmdParam.put(CMDPARAM_COMPLEMENT_DATA_START_DATE, DateUtil.format(start, Constants.YYYY_MM_DD_HH_MM_SS));
                    cmdParam.put(CMDPARAM_COMPLEMENT_DATA_END_DATE, DateUtil.format(end, Constants.YYYY_MM_DD_HH_MM_SS));
                    command.setCommandParam(JSONUtil.toJsonStr(cmdParam));
                    return commandService.save(command);
                } else if (runMode == RunMode.RUN_MODE_PARALLEL) {
                    List<Schedule> schedules = schedulesService.queryReleaseSchedulerListByProcessDefinitionId(processDefineId);
                    List<Date> listDate = new LinkedList<>();
                    if (!schedules.isEmpty()) {
                        for (Schedule item : schedules) {
                            listDate.addAll(CronUtils.getSelfFireDateList(start, end, item.getCrontab()));
                        }
                    }
                    if (!listDate.isEmpty()) {
                        // loop by schedule date
                        for (Date date : listDate) {
                            cmdParam.put(CMDPARAM_COMPLEMENT_DATA_START_DATE, DateUtil.format(date, Constants.YYYY_MM_DD_HH_MM_SS));
                            cmdParam.put(CMDPARAM_COMPLEMENT_DATA_END_DATE, DateUtil.format(date, Constants.YYYY_MM_DD_HH_MM_SS));
                            command.setCommandParam(JSONUtil.toJsonStr(cmdParam));
                            commandService.save(command);
                        }
                        return true;
                    } else {
                        // loop by day
                        int runCunt = 0;
                        while (!start.after(end)) {
                            runCunt += 1;
                            cmdParam.put(CMDPARAM_COMPLEMENT_DATA_START_DATE, DateUtil.format(start, Constants.YYYY_MM_DD_HH_MM_SS));
                            cmdParam.put(CMDPARAM_COMPLEMENT_DATA_END_DATE, DateUtil.format(start, Constants.YYYY_MM_DD_HH_MM_SS));
                            command.setCommandParam(JSONUtil.toJsonStr(cmdParam));
                            commandService.save(command);
                            start = DateUtils.addDays(start, 1);
                        }
                        return runCunt > 0;
                    }
                }
            } else {
                log.error("there is not valid schedule date for the process definition: id:{},date:{}",
                        processDefineId, schedule);
            }
        } else {
            command.setCommandParam(JSONUtil.toJsonStr(cmdParam));
            return commandService.save(command);
        }

        return false;
    }


}
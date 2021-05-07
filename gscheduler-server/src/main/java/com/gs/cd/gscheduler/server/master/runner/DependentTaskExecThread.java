/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.cd.gscheduler.server.master.runner;

import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.DependResult;
import com.gs.cd.gscheduler.common.enums.ExecutionStatus;
import com.gs.cd.gscheduler.common.model.DependentTaskModel;
import com.gs.cd.gscheduler.common.task.dependent.DependentParameters;
import com.gs.cd.gscheduler.common.thread.Stopper;
import com.gs.cd.gscheduler.common.utils.DependentUtils;
import com.gs.cd.gscheduler.common.utils.JSONUtils;
import com.gs.cd.gscheduler.common.utils.LoggerUtils;
import com.gs.cd.gscheduler.common.utils.OSUtils;
import com.gs.cd.gscheduler.dao.entity.TaskInstance;
import com.gs.cd.gscheduler.server.utils.DependentExecute;
import org.slf4j.LoggerFactory;

import java.util.*;

import static com.gs.cd.gscheduler.common.Constants.DEPENDENT_SPLIT;

public class DependentTaskExecThread extends MasterBaseTaskExecThread {

    private DependentParameters dependentParameters;

    /**
     * dependent task list
     */
    private List<DependentExecute> dependentTaskList = new ArrayList<>();

    /**
     * depend item result map
     * save the result to log file
     */
    private Map<String, DependResult> dependResultMap = new HashMap<>();


    /**
     * dependent date
     */
    private Date dependentDate;

    /**
     * constructor of MasterBaseTaskExecThread
     *
     * @param taskInstance    task instance
     */
    public DependentTaskExecThread(TaskInstance taskInstance) {
        super(taskInstance);
    }


    @Override
    public Boolean submitWaitComplete() {
        try{
            logger.info("dependent task start");
            this.taskInstance = submit();
            logger = LoggerFactory.getLogger(LoggerUtils.buildTaskId(LoggerUtils.TASK_LOGGER_INFO_PREFIX,
                    taskInstance.getProcessDefinitionId(),
                    taskInstance.getProcessInstanceId(),
                    taskInstance.getId()));
            String threadLoggerInfoName = String.format(Constants.TASK_LOG_INFO_FORMAT, processService.formatTaskAppId(this.taskInstance));
            Thread.currentThread().setName(threadLoggerInfoName);
            initTaskParameters();
            initDependParameters();
            waitTaskQuit();
            updateTaskState();
        }catch (Exception e){
            logger.error("dependent task run exception" , e);
        }
        return true;
    }

    /**
     * init dependent parameters
     */
    private void initDependParameters() {

        this.dependentParameters = JSONUtils.parseObject(this.taskInstance.getDependency(),
                DependentParameters.class);

        for(DependentTaskModel taskModel : dependentParameters.getDependTaskList()){
            this.dependentTaskList.add(new DependentExecute(
                    taskModel.getDependItemList(), taskModel.getRelation()));
        }
        if(this.processInstance.getScheduleTime() != null){
            this.dependentDate = this.processInstance.getScheduleTime();
        }else{
            this.dependentDate = new Date();
        }
    }

    /**
     *
     */
    private void updateTaskState() {
        ExecutionStatus status;
        if(this.cancel){
            status = ExecutionStatus.KILL;
        }else{
            DependResult result = getTaskDependResult();
            status = (result == DependResult.SUCCESS) ? ExecutionStatus.SUCCESS : ExecutionStatus.FAILURE;
        }
        taskInstance.setState(status);
        taskInstance.setEndTime(new Date());
        processService.saveTaskInstance(taskInstance);
    }

    /**
     * wait dependent tasks quit
     */
    private Boolean waitTaskQuit() {
        logger.info("wait depend task : {} complete", this.taskInstance.getName());
        if (taskInstance.getState().typeIsFinished()) {
            logger.info("task {} already complete. task state:{}",
                    this.taskInstance.getName(),
                    this.taskInstance.getState());
            return true;
        }
        while (Stopper.isRunning()) {
            try{
                if(this.processInstance == null){
                    logger.error("process instance not exists , master task exec thread exit");
                    return true;
                }
                if (checkTaskTimeout()) {
//                    this.checkTimeoutFlag = !alertTimeout();
                    handleTimeoutFailed();
                }
                if(this.cancel || this.processInstance.getState() == ExecutionStatus.READY_STOP){
                    cancelTaskInstance();
                    break;
                }

                if ( allDependentTaskFinish() || taskInstance.getState().typeIsFinished()){
                    break;
                }
                // update process task
                taskInstance = processService.findTaskInstanceById(taskInstance.getId());
                processInstance = processService.findProcessInstanceById(processInstance.getId());
                Thread.sleep(Constants.SLEEP_TIME_MILLIS);
            } catch (Exception e) {
                logger.error("exception",e);
                if (processInstance != null) {
                    logger.error("wait task quit failed, instance id:{}, task id:{}",
                            processInstance.getId(), taskInstance.getId());
                }
            }
        }
        return true;
    }

    /**
     * cancel dependent task
     */
    private void cancelTaskInstance() {
        this.cancel = true;
    }

    private void initTaskParameters() {
        taskInstance.setLogPath(getTaskLogPath(taskInstance));
        taskInstance.setHost(OSUtils.getHost() + Constants.COLON + masterConfig.getListenPort());
        taskInstance.setState(ExecutionStatus.RUNNING_EXEUTION);
        taskInstance.setStartTime(new Date());
        processService.updateTaskInstance(taskInstance);
    }

    /**
     * judge all dependent tasks finish
     * @return whether all dependent tasks finish
     */
    private boolean allDependentTaskFinish(){
        boolean finish = true;
        for(DependentExecute dependentExecute : dependentTaskList){
            for(Map.Entry<String, DependResult> entry: dependentExecute.getDependResultMap().entrySet()) {
                if(!dependResultMap.containsKey(entry.getKey())){
                    dependResultMap.put(entry.getKey(), entry.getValue());
                    //save depend result to log
                    logger.info("dependent item complete {} {},{}",
                            DEPENDENT_SPLIT, entry.getKey(), entry.getValue());
                }
            }
            if(!dependentExecute.finish(dependentDate)){
                finish = false;
            }
        }
        return finish;
    }

    /**
     * get dependent result
     * @return DependResult
     */
    private DependResult getTaskDependResult(){
        List<DependResult> dependResultList = new ArrayList<>();
        for(DependentExecute dependentExecute : dependentTaskList){
            DependResult dependResult = dependentExecute.getModelDependResult(dependentDate);
            dependResultList.add(dependResult);
        }
        DependResult result = DependentUtils.getDependResultForRelation(
                this.dependentParameters.getRelation(), dependResultList
        );
        logger.info("dependent task completed, dependent result:{}", result);
        return result;
    }
}
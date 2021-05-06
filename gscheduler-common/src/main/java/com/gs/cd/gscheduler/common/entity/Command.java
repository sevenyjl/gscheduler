package com.gs.cd.gscheduler.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gs.cd.gscheduler.common.enums.*;
import lombok.Data;

import java.util.Date;

/**
 * command
 */
@TableName("t_ds_command")
@Data
public class Command {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * command type
     */
    @TableField("command_type")
    private CommandType commandType;

    /**
     * process definition id
     */
    @TableField("process_definition_id")
    private int processDefinitionId;

    /**
     * executor id
     */
    @TableField("executor_id")
    private String executorId;

    /**
     * command parameter, format json
     */
    @TableField("command_param")
    private String commandParam;

    /**
     * task depend type
     */
    @TableField("task_depend_type")
    private TaskDependType taskDependType;

    /**
     * failure strategy
     */
    @TableField("failure_strategy")
    private FailureStrategy failureStrategy;

    /**
     * warning type
     */
    @TableField("warning_type")
    private WarningType warningType;

    /**
     * warning group id
     */
    @TableField("warning_group_id")
    private Integer warningGroupId;

    /**
     * schedule time
     */
    @TableField("schedule_time")
    private Date scheduleTime;

    /**
     * start time
     */
    @TableField("start_time")
    private Date startTime;

    /**
     * process instance priority
     */
    @TableField("process_instance_priority")
    private Priority processInstancePriority;

    /**
     * update time
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * worker group
     */
    @TableField("worker_group")
    private String workerGroup;

    public Command() {
        this.taskDependType = TaskDependType.TASK_POST;
        this.failureStrategy = FailureStrategy.CONTINUE;
        this.startTime = new Date();
        this.updateTime = new Date();
    }

    public Command(
            CommandType commandType,
            TaskDependType taskDependType,
            FailureStrategy failureStrategy,
            String executorId,
            int processDefinitionId,
            String commandParam,
            WarningType warningType,
            int warningGroupId,
            Date scheduleTime,
            String workerGroup,
            Priority processInstancePriority) {
        this.commandType = commandType;
        this.executorId = executorId;
        this.processDefinitionId = processDefinitionId;
        this.commandParam = commandParam;
        this.warningType = warningType;
        this.warningGroupId = warningGroupId;
        this.scheduleTime = scheduleTime;
        this.taskDependType = taskDependType;
        this.failureStrategy = failureStrategy;
        this.startTime = new Date();
        this.updateTime = new Date();
        this.workerGroup = workerGroup;
        this.processInstancePriority = processInstancePriority;
    }


}

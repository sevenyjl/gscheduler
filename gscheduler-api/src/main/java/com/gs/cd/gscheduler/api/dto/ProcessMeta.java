package com.gs.cd.gscheduler.api.dto;

import lombok.Data;

/**
 * @Author seven
 * @Date 2021/4/29 14:27
 * @Description
 * @Version 1.0
 */
@Data
public class ProcessMeta {

    /**
     * project name
     */
    private String projectName;

    /**
     * process definition name
     */
    private String processDefinitionName;

    /**
     * processs definition json
     */
    private String processDefinitionJson;

    /**
     * process definition desc
     */
    private String processDefinitionDescription;

    /**
     * process definition locations
     */
    private String processDefinitionLocations;

    /**
     * process definition connects
     */
    private String processDefinitionConnects;

    /**
     * warning type
     */
    private String scheduleWarningType;

    /**
     * warning group id
     */
    private Integer scheduleWarningGroupId;

    /**
     * warning group name
     */
    private String scheduleWarningGroupName;

    /**
     * start time
     */
    private String scheduleStartTime;

    /**
     * end time
     */
    private String scheduleEndTime;

    /**
     * crontab
     */
    private String scheduleCrontab;

    /**
     * failure strategy
     */
    private String scheduleFailureStrategy;

    /**
     * release state
     */
    private String scheduleReleaseState;

    /**
     * process instance priority
     */
    private String scheduleProcessInstancePriority;

    /**
     * worker group name
     */
    private String scheduleWorkerGroupName;
}

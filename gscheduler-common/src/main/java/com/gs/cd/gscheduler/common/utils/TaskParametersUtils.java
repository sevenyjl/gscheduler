package com.gs.cd.gscheduler.common.utils;

import cn.hutool.json.JSONUtil;
import com.gs.cd.gscheduler.common.enums.TaskType;
import com.gs.cd.gscheduler.common.task.AbstractParameters;
import com.gs.cd.gscheduler.common.task.conditions.ConditionsParameters;
import com.gs.cd.gscheduler.common.task.datacollector.DataCollectorParameters;
import com.gs.cd.gscheduler.common.task.http.HttpParameters;
import com.gs.cd.gscheduler.common.task.shell.ShellParameters;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * task parameters utils
 */
public class TaskParametersUtils {

    private static Logger logger = LoggerFactory.getLogger(TaskParametersUtils.class);

    /**
     * get task parameters
     *
     * @param taskType  task type
     * @param parameter parameter
     * @return task parameters
     */
    public static AbstractParameters getParameters(String taskType, String parameter) {
        try {
            switch (EnumUtils.getEnum(TaskType.class, taskType)) {

                case SHELL:
                    return JSONUtil.toBean(parameter, ShellParameters.class);
                case HTTP:
                    return JSONUtil.toBean(parameter, HttpParameters.class);
                case CONDITIONS:
                    return JSONUtil.toBean(parameter, ConditionsParameters.class);
                case DATA_COLLECTOR:
                    return JSONUtil.toBean(parameter, DataCollectorParameters.class);
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}

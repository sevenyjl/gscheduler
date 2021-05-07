package com.gs.cd.gscheduler.server.worker.task.expand;

import com.gs.cd.gscheduler.common.task.AbstractParameters;
import com.gs.cd.gscheduler.common.task.expand.DataCollectorParameters;
import com.gs.cd.gscheduler.common.utils.JSONUtils;
import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;
import com.gs.cd.gscheduler.server.worker.task.AbstractTask;
import org.slf4j.Logger;

/**
 * @Author seven
 * @Date 2021/1/12 11:30
 * @Description 数据收集器任务
 * @Version 1.0
 */
public class DataCollectorTask extends AbstractTask {
    private DataCollectorParameters dataCollectorParameters;
    private TaskExecutionContext taskExecutionContext;


    /**
     * constructor
     *
     * @param taskExecutionContext taskExecutionContext
     * @param logger               logger
     */
    public DataCollectorTask(TaskExecutionContext taskExecutionContext, Logger logger) {
        super(taskExecutionContext, logger);
        this.taskExecutionContext = taskExecutionContext;
    }

    @Override
    public void init() {
        logger.info("DATA_COLLECTOR task params {}", taskExecutionContext.getTaskParams());
        this.dataCollectorParameters = JSONUtils.parseObject(taskExecutionContext.getTaskParams(), DataCollectorParameters.class);
        if (!dataCollectorParameters.checkParameters()) {
            throw new RuntimeException("DATA_COLLECTOR task params is not valid");
        }
    }

    @Override
    public void handle() throws Exception {
        try {
            String collectorId = this.dataCollectorParameters.getPrepareJson();
            String taskName = String.format("Job-%s-%s", taskExecutionContext.getTaskAppId(), taskExecutionContext.getTaskName());
            exitStatusCode = new DataCollectorHandler(collectorId, taskName, logger).handle();
        } catch (Exception e) {
            logger.error(e.getMessage());
            exitStatusCode = -1;
        }
    }

    @Override
    public AbstractParameters getParameters() {
        return this.dataCollectorParameters;
    }
}

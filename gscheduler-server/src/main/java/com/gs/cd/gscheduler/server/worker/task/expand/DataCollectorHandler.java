package com.gs.cd.gscheduler.server.worker.task.expand;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.gscheduler.common.utils.FileUtils;
import com.gs.cd.gscheduler.common.utils.JSONUtils;
import com.gs.cd.gscheduler.common.utils.LoggerUtils;
import com.gs.cd.gscheduler.server.worker.etl.ETLUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Create By Hippo 2021/3/2
 *
 * @Description:
 */
public class DataCollectorHandler {

    private final List<String> jobEndStatusList = Arrays.asList("FAILING", "FAILED", "CANCELLING", "CANCELED", "FINISHED");

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String collectorId;
    private final String taskName;
    private Logger logger;

    public DataCollectorHandler(String collectorId, String taskName, Logger logger) {
        this.collectorId = collectorId;
        this.taskName = taskName;
        this.logger = logger;
    }

    public int handle() {
        try {
            String[] split = this.collectorId.split("\\|\\|");
            if (split.length != 2) {
                return 0;
            }
            LoggerUtils.logInfo(Optional.ofNullable(FileUtils.logger), String.format("查询租户code=%s，收集器id=%s的信息", split[0], split[1]));
            String pluginJsonString = getApiResultData(ETLUtils.gSnowService.getEtlJsonById(split[1], split[0]));
            String etlTaskJson = prepareJson2EtlTask(taskName, pluginJsonString);
            String jobId = getApiResultData(ETLUtils.submitJob(etlTaskJson));
            return getJobStatusUntilTaskEnd(jobId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return -1;
        }
    }


    private String getApiResultData(ApiResult apiResult) {
        if (!apiResult.isSuccess()) {
            logger.error(String.format("调用服务接口失败：%s", apiResult));
            throw new IllegalArgumentException(String.format("调用ETL服务接口失败：%s", apiResult));
        }
        logger.info("调用服务成功，data信息：" + apiResult.getData());
        return apiResult.getData().toString();
    }

    private int getJobStatusUntilTaskEnd(String jobId) {
        while (true) {
            String statusString = getApiResultData(ETLUtils.checkJobStatus(jobId));
            if (statusString.contains("FAILED"))return -1;
            if (!jobEndStatusList.contains(statusString.toUpperCase())) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    //nothing
                    return -1;
                }
            } else {
                return 0;
            }
        }
    }

    private String prepareJson2EtlTask(String taskName, String pluginJsonString) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("name", taskName);
        // TODO: 2021/2/23 前端 应该要配置channel信息
        jsonObject.putOpt("settings", JSONUtils.parseObject("{\"channel\":1}"));
        JSONObject jsonObject1 = JSONUtil.parseObj(pluginJsonString);
        jsonObject.putOpt("plugin", jsonObject1);
        String s = JSONUtil.toJsonStr(jsonObject);
        LoggerUtils.logInfo(Optional.ofNullable(FileUtils.logger), "发送etl json:\n" + s);
        return s;
    }
}

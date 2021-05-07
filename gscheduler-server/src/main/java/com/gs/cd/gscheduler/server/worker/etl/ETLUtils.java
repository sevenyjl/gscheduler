package com.gs.cd.gscheduler.server.worker.etl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.utils.URIUtils;
import com.gs.cd.gsnow.api.service.GSnowService;
import lombok.extern.slf4j.Slf4j;
import com.gs.cd.gscheduler.common.utils.FileUtils;
import com.gs.cd.gscheduler.common.utils.LoggerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Optional;

/**
 * @Author seven
 * @Date 2021/2/18 16:00
 * @Description
 * @Version 1.0
 */
@Component
@Slf4j
public class ETLUtils {


    public static GSnowService gSnowService;
    private static ETLConfig etlConfig;
    @Autowired
    ApplicationContext applicationContext;


    @PostConstruct
    public void init() {
        gSnowService = applicationContext.getBean(GSnowService.class);
        etlConfig = applicationContext.getBean(ETLConfig.class);
        log.info(String.format("\n====================\n" +
                        "=\tGSnow server:%s\t=\n" +
                        "=\tGEtl server:%s\t=\n" +
                        "====================\n",
                etlConfig.getGsnowApiIp() + ":" + etlConfig.getGsnowApiPort(), etlConfig.getEtlServiceUrl()
        ));
    }

    /**
     * 调用etl api 运行任务
     *
     * @param json
     * @return
     */
    public static ApiResult submitJob(String json) {
        String url = URIUtils.getUrl(etlConfig.getEtlServiceUrl(), etlConfig.getSubmitJobUrl());
        try {
            return JSONUtil.toBean(HttpUtil.post(url, json), ApiResult.class);
        } catch (Exception e) {
            LoggerUtils.logError(Optional.ofNullable(FileUtils.logger), "请求etl地址：" + url + "数据：\n" + json);
            log.error("请求etl地址：" + url + "数据：\n" + json);
            return ApiResult.error("etl错误：" + e.getMessage());
        }
    }

    public static ApiResult checkJobStatus(String jobId) {
        String url = URIUtils.getUrl(etlConfig.getEtlServiceUrl(), etlConfig.getCheckJobStatusUrl(), jobId);
        try {
            return JSONUtil.toBean(HttpUtil.get(url), ApiResult.class);
        } catch (Exception e) {
            LoggerUtils.logError(Optional.ofNullable(FileUtils.logger), "etl检测job状态,请求地址：" + url);
            log.error("etl检测job状态,请求地址：" + url);
            return ApiResult.error("etl错误：" + e.getMessage());
        }
    }
}

package com.gs.cd.gscheduler.server.worker.etl;

import com.gs.cd.gsnow.api.service.GSnowService;
import com.gs.cd.gsnow.api.service.impl.GSnowServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author seven
 * @Date 2021/2/18 15:05
 * @Description
 * @Version 1.0
 */
@Configuration
@PropertySource(value = "worker.properties")
public class ETLConfig {

    @Value("${gsnow.api.ip:127.0.0.1}")
    private String gsnowApiIp;
    @Value("${gsnow.api.port:10101}")
    private String gsnowApiPort;


    @Value("${getl.server.url:http://127.0.0.1:8080}")
    private String etlServiceUrl;

    @Value("${getl.server.url.submitJob:/getl/launcher/simple}")
    private String submitJobUrl;

    @Value("${getl.server.url.checkJobStatus:/getl/launcher/job/info}")
    private String checkJobStatusUrl;

    @Bean
    public GSnowService gSnowService() {
        return new GSnowServiceImpl(gsnowApiIp + ":" + gsnowApiPort);
    }

    public String getGsnowApiIp() {
        return gsnowApiIp;
    }

    public void setGsnowApiIp(String gsnowApiIp) {
        this.gsnowApiIp = gsnowApiIp;
    }

    public String getGsnowApiPort() {
        return gsnowApiPort;
    }

    public void setGsnowApiPort(String gsnowApiPort) {
        this.gsnowApiPort = gsnowApiPort;
    }

    public String getEtlServiceUrl() {
        return etlServiceUrl;
    }

    public void setEtlServiceUrl(String etlServiceUrl) {
        this.etlServiceUrl = etlServiceUrl;
    }

    public String getSubmitJobUrl() {
        return submitJobUrl;
    }

    public void setSubmitJobUrl(String submitJobUrl) {
        this.submitJobUrl = submitJobUrl;
    }

    public String getCheckJobStatusUrl() {
        return checkJobStatusUrl;
    }

    public void setCheckJobStatusUrl(String checkJobStatusUrl) {
        this.checkJobStatusUrl = checkJobStatusUrl;
    }
}

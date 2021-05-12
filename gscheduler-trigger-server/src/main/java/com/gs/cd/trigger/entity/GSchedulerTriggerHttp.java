package com.gs.cd.trigger.entity;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.dolphinscheduler.common.Constants;
import org.apache.dolphinscheduler.common.enums.HttpCheckCondition;
import org.apache.dolphinscheduler.common.enums.HttpMethod;
import org.apache.dolphinscheduler.common.enums.HttpParametersType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author seven
 * @Date 2021/5/10 16:54
 * @Description
 * @Version 1.0
 */
/**
 * create:
 *
 * {"globalParams":[],"tasks":[{"type":"HTTP","id":"tasks-44133","name":"http","params":{"localParams":[],"httpParams":[],"url":"http://10.202.40.72:17836/","httpMethod":"GET","httpCheckCondition":"STATUS_CODE_DEFAULT","condition":""},"description":"http","timeout":{"strategy":"","interval":null,"enable":false},"runFlag":"NORMAL","conditionResult":{"successNode":[""],"failedNode":[""]},"dependence":{},"maxRetryTimes":"0","retryInterval":"1","taskInstancePriority":"MEDIUM","workerGroup":"default","preTasks":[]}],"tenantId":4,"timeout":0}
 *
 * {"startTime":"2021-05-11 00:00:00","endTime":"2121-05-11 00:00:00","crontab":"0 0 * * * ? *"}
 */
@Data
public class GSchedulerTriggerHttp {
    private String groupName;
    private String taskId;
    private String url;
    private List<HttpData> httpParams = new ArrayList<>();
    private HttpMethod httpMethod = HttpMethod.GET;
    private HttpCheckCondition httpCheckCondition = HttpCheckCondition.STATUS_CODE_DEFAULT;

    private Date startTime = new Date();
    private Date endTime = new Date(System.currentTimeMillis() + 10000);
    private String crontab;

    public String getProcessName() {
        return groupName + "_" + taskId;
    }

    // TODO: 2021/5/11 海豚初始化sql初始化 一个 id=0的租户
    public String getProcessDefinitionJson() {
        final String s = "{\"globalParams\":[],\"tasks\":[{\"type\":\"HTTP\",\"id\":\"%s\",\"name\":\"%s\",\"params\":{\"localParams\":[],\"httpParams\":%s,\"url\":\"%s\",\"httpMethod\":\"%s\",\"httpCheckCondition\":\"%s\",\"condition\":\"\"},\"description\":\"定时触发器\",\"timeout\":{\"strategy\":\"\",\"interval\":null,\"enable\":false},\"runFlag\":\"NORMAL\",\"conditionResult\":{\"successNode\":[\"\"],\"failedNode\":[\"\"]},\"dependence\":{},\"maxRetryTimes\":\"0\",\"retryInterval\":\"1\",\"taskInstancePriority\":\"MEDIUM\",\"workerGroup\":\"default\",\"preTasks\":[]}],\"tenantId\":0,\"timeout\":0}";
        return String.format(s, taskId, taskId, JSONUtil.toJsonStr(httpParams), url, httpMethod.name(), httpCheckCondition.name());
    }

    public String getScheduler() {
        final String s = "{\"startTime\":\"%s\",\"endTime\":\"%s\",\"crontab\":\"%s\"}";
        return String.format(s, DateUtil.format(startTime, Constants.YYYY_MM_DD_HH_MM_SS), DateUtil.format(endTime, Constants.YYYY_MM_DD_HH_MM_SS)
                , crontab);
    }
}

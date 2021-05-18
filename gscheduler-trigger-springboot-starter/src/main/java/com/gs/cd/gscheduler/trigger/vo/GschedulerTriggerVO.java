package com.gs.cd.gscheduler.trigger.vo;

import cn.hutool.json.JSONUtil;
import com.gs.cd.gscheduler.trigger.enums.TriggerType;
import lombok.Data;
import lombok.NonNull;

import java.util.Date;

/**
 * @Author seven
 * @Date 2021/4/27 15:45
 * @Description
 * @Version 1.0
 */
@Data
public class GschedulerTriggerVO {
    private Integer id;

    private final String taskId;

    private final String groupName;

    private final String corn;

    private final String params;

    private final HttpParams httpParams;

    private final TriggerType type;

    private Date startTime;

    private Date endTime;

    private boolean suspendFlag = false;
    ;


    private String nacosServiceName;
    private String clusterName;
    private String nameSpaceId;

    public String getNacosServiceName() {
        return nacosServiceName;
    }

    public void setNacosServiceName(String nacosServiceName) {
        this.nacosServiceName = nacosServiceName;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNameSpaceId() {
        return nameSpaceId;
    }

    public void setNameSpaceId(String nameSpaceId) {
        this.nameSpaceId = nameSpaceId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public GschedulerTriggerVO(String taskId, String groupName, String corn, String params, HttpParams httpParams, TriggerType type) {
        this.taskId = taskId;
        this.groupName = groupName;
        this.corn = corn;
        this.params = params;
        this.httpParams = httpParams;
        this.type = type;
    }

    public GschedulerTriggerVO(@NonNull Integer id, @NonNull String taskId, @NonNull String groupName,
                               @NonNull String corn, @NonNull HttpParams httpParams) {
        this.id = id;
        this.taskId = taskId;
        this.groupName = groupName;
        this.corn = corn;
        this.httpParams = httpParams;
        this.type = TriggerType.HTTP;
        this.params = JSONUtil.toJsonStr(httpParams);
    }

    public GschedulerTriggerVO(@NonNull String taskId,
                               @NonNull String groupName, @NonNull String corn,
                               @NonNull HttpParams httpParams
    ) {
        this.groupName = groupName;
        this.taskId = taskId;
        this.corn = corn;
        this.httpParams = httpParams;
        this.type = TriggerType.HTTP;
        this.params = JSONUtil.toJsonStr(httpParams);
    }
}
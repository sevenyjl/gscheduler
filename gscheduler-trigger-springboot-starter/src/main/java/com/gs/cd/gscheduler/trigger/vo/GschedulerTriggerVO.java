package com.gs.cd.gscheduler.trigger.vo;

import cn.hutool.json.JSONUtil;
import com.gs.cd.gscheduler.common.enums.TriggerType;
import com.gs.cd.gscheduler.common.enums.entity.HttpParams;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

import java.util.Date;

/**
 * @Author seven
 * @Date 2021/4/27 15:45
 * @Description
 * @Version 1.0
 */
@Getter
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

    public GschedulerTriggerVO(@NonNull Integer id,@NonNull  String taskId,@NonNull  String groupName,
                               @NonNull  String corn,@NonNull  HttpParams httpParams) {
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

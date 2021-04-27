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

    private String taskId;

    private String corn;

    private Date startTime;

    private Date endTime;

    private String params;

    private HttpParams httpParams;

    private TriggerType type;

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

    public GschedulerTriggerVO(@NonNull String taskId, @NonNull String corn,
                               @NonNull HttpParams httpParams, @NonNull TriggerType type) {
        this.taskId = taskId;
        this.corn = corn;
        this.httpParams = httpParams;
        this.type = type;
        this.params = JSONUtil.toJsonStr(httpParams);
    }
}

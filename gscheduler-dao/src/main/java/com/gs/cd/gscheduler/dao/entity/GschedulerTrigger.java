package com.gs.cd.gscheduler.dao.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gs.cd.gscheduler.common.TriggerType;
import com.gs.cd.gscheduler.common.entity.HttpParams;
import com.gs.cd.gscheduler.common.entity.ITrigger;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
@TableName("gscheduler_trigger")
@Data
public class GschedulerTrigger implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String groupName = "DEFAULT";

    private String corn;

    private Date createTime;

    private Date updateTime;

    private Date startTime = new Date();

    private Date endTime = new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365);

    private String params;

    private TriggerType type;

    public static ITrigger params2ITrigger(GschedulerTrigger gschedulerTrigger) {
        switch (gschedulerTrigger.getType()) {
            case HTTP:
                return JSONUtil.toBean(gschedulerTrigger.getParams(), HttpParams.class);
            default:
                throw new RuntimeException("未知类型错误，TriggerType=" + gschedulerTrigger.getType());
        }
    }
}

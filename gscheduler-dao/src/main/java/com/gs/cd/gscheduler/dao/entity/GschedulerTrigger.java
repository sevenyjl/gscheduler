package com.gs.cd.gscheduler.dao.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.gs.cd.gscheduler.common.enums.TriggerType;
import com.gs.cd.gscheduler.common.enums.entity.HttpParams;
import com.gs.cd.gscheduler.common.enums.entity.ITrigger;
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

    private String corn;

    private Date createTime;

    private Date updateTime;

    private Date startTime;

    private Date endTime;

    private String params;

    private TriggerType type;

    public ITrigger params2ITrigger() {
        switch (type) {
            case HTTP:
                return JSONUtil.toBean(params, HttpParams.class);
            default:
                throw new RuntimeException("未知类型错误，TriggerType=" + type);
        }
    }
}

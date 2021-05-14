
package com.gs.cd.gscheduler.trigger.server.entity;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.gs.cd.gscheduler.trigger.server.enums.TriggerType;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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

    @TableField(exist = false)
    private HttpParams httpParams;

    private TriggerType type;

    private Boolean lockFlag;

    private String tenantCode;

    private String address;

    @TableField(exist = false)
    private String nacosServiceName;
    @TableField(exist = false)
    private String clusterName;
    @TableField(exist = false)
    private String nameSpaceId;

    public String iTrigger2Params() {
        if (httpParams != null) {
            try {
                String s = JSONUtil.toJsonStr(httpParams);
                this.params = s;
                return s;
            } catch (Exception e) {
                log.error("iTrigger序列化错误{}", httpParams);
            }
        }
        return this.params;
    }

    public ITrigger params2ITrigger() {
        if (params != null) {
            try {
                switch (type) {
                    case HTTP:
                        return JSONUtil.toBean(params, HttpParams.class);
                    default:
                        throw new RuntimeException("未知类型错误，TriggerType=" + type);
                }
            } catch (Exception e) {
                log.error("反序列化错误{}", params);
            }
        }
        return this.httpParams;
    }

}

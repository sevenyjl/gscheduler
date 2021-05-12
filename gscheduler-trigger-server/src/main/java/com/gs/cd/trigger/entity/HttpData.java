package com.gs.cd.trigger.entity;

import lombok.Data;
import org.apache.dolphinscheduler.common.enums.HttpParametersType;

/**
 * @Author seven
 * @Date 2021/5/10 17:53
 * @Description
 * @Version 1.0
 */
@Data
public class HttpData {

    private String prop;
    private String value;
    private HttpParametersType httpParametersType;
}

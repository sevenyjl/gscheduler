package com.gs.cd.gscheduler.common.task.datacollector;

import cn.hutool.core.util.StrUtil;
import com.gs.cd.gscheduler.common.process.ResourceInfo;
import com.gs.cd.gscheduler.common.task.AbstractParameters;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author seven
 * @Date 2021/1/12 11:32
 * @Description 数据收集器参数
 * @Version 1.0
 */
@Slf4j
public class DataCollectorParameters extends AbstractParameters {
    private String prepareJson;

    public String getPrepareJson() {
        return prepareJson;
    }

    public void setPrepareJson(String prepareJson) {
        this.prepareJson = prepareJson;
    }

    @Override
    public boolean checkParameters() {
        log.debug("参数信息：{}", prepareJson);
        return StrUtil.isNotEmpty(prepareJson);
    }

    @Override
    public List<ResourceInfo> getResourceFilesList() {
        return new ArrayList<>();
    }
}

package com.gs.cd.gscheduler.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.common.entity.ProcessInstance;
import com.gs.cd.gscheduler.common.entity.Project;
import com.gs.cd.gscheduler.common.enums.Flag;

import java.util.Map;

/**
 * @Author seven
 * @Date 2021/4/29 14:46
 * @Description
 * @Version 1.0
 */
public interface ProcessInstanceService extends IService<ProcessInstance> {
    Map<String, Object> queryTaskListByProcessId(String projectName, Integer processInstanceId);

    boolean updateProcessInstance(JwtUserInfo loginUser, String projectName, Integer processInstanceId, String processInstanceJson, String scheduleTime, Boolean syncDefine, Flag flag, String locations, String connects);
}

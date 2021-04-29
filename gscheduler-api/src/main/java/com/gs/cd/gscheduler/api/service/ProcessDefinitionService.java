package com.gs.cd.gscheduler.api.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.common.ApiResult;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.common.entity.GschedulerTrigger;
import com.gs.cd.gscheduler.common.entity.ProcessDefinition;
import com.gs.cd.gscheduler.common.model.TaskNode;
import com.gs.cd.gscheduler.dao.mapper.ProcessDefinitionMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 * @since 2021-04-27
 */
public interface ProcessDefinitionService extends IService<ProcessDefinition> {

    boolean createProcessDefinition(JwtUserInfo loginUser, String projectName,
                                    String name, String json, String description,
                                    String locations, String connects);

    boolean copyProcessDefinition(JwtUserInfo loginUser, String projectName, int processId);

    ApiResult verifyProcessDefinitionName(JwtUserInfo loginUser, String projectName, String name);

    boolean updateProcessDefinition(JwtUserInfo loginUser, String projectName,
                                    int id, String name, String processDefinitionJson,
                                    String description, String locations, String connects);

    boolean releaseProcessDefinition(JwtUserInfo loginUser, String projectName, int processId, int releaseState);

    List<ProcessDefinition> listByProjectName(String projectName);

    List<TaskNode> getTaskNodeListByDefinitionId(Integer processDefinitionId);

    Map<Integer, List<TaskNode>> getTaskNodeListByDefinitionIdList(String processDefinitionIdList);

    void batchExportProcessDefinitionByIds( String projectName, String processDefinitionIds, HttpServletResponse response);
}


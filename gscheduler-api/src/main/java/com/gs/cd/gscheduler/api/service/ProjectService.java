package com.gs.cd.gscheduler.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.common.entity.Project;

import java.util.Map;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author
 * @since 2021-04-23
 */
public interface ProjectService extends IService<Project> {

    boolean createProject(JwtUserInfo creator, String projectName, String description);


    boolean updateProject(JwtUserInfo userInfo, Integer projectId, String projectName, String description);

    Project queryByName(String projectName);
}

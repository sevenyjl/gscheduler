package com.gs.cd.gscheduler.api.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.service.ProjectService;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.entity.Project;
import com.gs.cd.gscheduler.dao.mapper.ProjectMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author
 * @since 2021-04-23
 */
@Service
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Override
    public boolean createProject(JwtUserInfo loginUser, String name, String desc) {
        if (queryByName(name) != null) {
            throw new RuntimeException(String.format("名称为\"%s\"的项目已经存在", name));
        }
        Project project = new Project();
        Date now = new Date();
        project.setName(name);
        project.setDescription(desc);
        project.setCreator(loginUser.getUserName());
        project.setUpdater(loginUser.getUserName());
        project.setUserId(loginUser.getUserId().toString());
        project.setCreateTime(now);
        project.setUpdateTime(now);
        return save(project);
    }

    @Override
    public boolean updateProject(JwtUserInfo loginUser, Integer projectId, String projectName, String desc) {
        Project project = getById(projectId);
        if (project == null) {
            throw new RuntimeException(String.format("修改失败,无id=%s的项目", projectId));
        }
        Project tempProject = queryByName(projectName);
        if (tempProject != null) {
            throw new RuntimeException(String.format("名称为\"%s\"的项目已经存在", projectName));
        }
        project.setName(projectName);
        project.setDescription(desc);
        project.setUpdateTime(new Date());
        project.setUpdater(loginUser.getUserName());
        return updateById(project);
    }

    @Override
    public Project queryByName(String projectName) {
        if (StrUtil.isEmpty(projectName)) return null;
        return getOne(new QueryWrapper<Project>().lambda().eq(Project::getName, projectName));
    }

}

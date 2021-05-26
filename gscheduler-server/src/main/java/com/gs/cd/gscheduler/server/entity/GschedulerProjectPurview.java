package com.gs.cd.gscheduler.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author seven
 * @Date 2021/5/13 17:36
 * @Description
 * @Version 1.0
 */
@Data
@TableName("gscheduler_project_purview")
public class GschedulerProjectPurview {

    public static final String add = "taskScheduling:projectManagement:add";
    public static final String view = "taskScheduling:projectManagement:view";
    public static final String edit = "taskScheduling:projectManagement:edit";
    public static final String delete = "taskScheduling:projectManagement:delete";
    public static final String configurePermissions = "taskScheduling:projectManagement:configurePermissions";

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer projectId;
    private String userGroupId;
    private String roleId;
    private String projectName;
}

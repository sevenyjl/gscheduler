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

    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer projectId;
    private String userGoupId;
    private String roleId;
}

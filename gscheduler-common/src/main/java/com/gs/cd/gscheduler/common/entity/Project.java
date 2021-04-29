/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gs.cd.gscheduler.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * project
 */
@Data
@TableName("t_ds_project")
public class Project {

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private int id;

    /**
     * user id
     */
    @TableField("user_id")
    private String userId;

    /**
     * user name
     */
    @TableField(exist = false)
    private String userName;

    /**
     * project name
     */
    private String name;

    /**
     * project description
     */
    private String description;

    /**
     * create time
     */
    private Date createTime;

    /**
     * update time
     */
    private Date updateTime;

    private String creator;
    private String updater;

    /**
     * permission
     */
    @TableField(exist = false)
    private int perm;

    /**
     * process define count
     */
    @TableField(exist = false)
    private int defCount;

    /**
     * process instance running count
     */
    @TableField(exist = false)
    private int instRunningCount;


}

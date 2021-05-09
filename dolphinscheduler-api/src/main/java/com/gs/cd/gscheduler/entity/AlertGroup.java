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
package com.gs.cd.gscheduler.entity;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gs.cd.gscheduler.config.DateJsonDateDeserializer;
import org.apache.dolphinscheduler.common.enums.AlertType;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;


public class AlertGroup {
    /**
     * primary key
     */

    private int id;
    /**
     * group_name
     */

    private String groupName;
    /**
     * group_type
     */

    private AlertType groupType;
    /**
     * description
     */

    private String description;
    /**
     * create_time
     */

    private Date createTime;
    /**
     * update_time
     */

    private Date updateTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public AlertType getGroupType() {
        return groupType;
    }

    public void setGroupType(AlertType groupType) {
        this.groupType = groupType;
    }

    public Date getCreateTime() {
        return createTime;
    }
    @JsonDeserialize(using = DateJsonDateDeserializer.class)
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }
    @JsonDeserialize(using = DateJsonDateDeserializer.class)
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlertGroup that = (AlertGroup) o;

        if (id != that.id) {
            return false;
        }
        if (groupName != null ? !groupName.equals(that.groupName) : that.groupName != null) {
            return false;
        }
        if (groupType != that.groupType) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        return !(createTime != null ? !createTime.equals(that.createTime) : that.createTime != null) && !(updateTime != null ? !updateTime.equals(that.updateTime) : that.updateTime != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (groupName != null ? groupName.hashCode() : 0);
        result = 31 * result + (groupType != null ? groupType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (createTime != null ? createTime.hashCode() : 0);
        result = 31 * result + (updateTime != null ? updateTime.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "AlertGroup{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", groupType=" + groupType +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}

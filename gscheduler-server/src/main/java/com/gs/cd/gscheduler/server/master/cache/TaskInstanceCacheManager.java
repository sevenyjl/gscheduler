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

package com.gs.cd.gscheduler.server.master.cache;

import com.gs.cd.gscheduler.dao.entity.TaskInstance;
import com.gs.cd.gscheduler.remote.command.TaskExecuteAckCommand;
import com.gs.cd.gscheduler.remote.command.TaskExecuteResponseCommand;
import com.gs.cd.gscheduler.server.entity.TaskExecutionContext;

/**
 *  task instance state manager
 */
public interface TaskInstanceCacheManager {

    /**
     * get taskInstance by taskInstance id
     *
     * @param taskInstanceId taskInstanceId
     * @return taskInstance
     */
    TaskInstance getByTaskInstanceId(Integer taskInstanceId);

    /**
     * cache taskInstance
     *
     * @param taskExecutionContext taskExecutionContext
     */
    void cacheTaskInstance(TaskExecutionContext taskExecutionContext);

    /**
     * cache taskInstance
     *
     * @param taskAckCommand taskAckCommand
     */
    void cacheTaskInstance(TaskExecuteAckCommand taskAckCommand);

    /**
     * cache taskInstance
     *
     * @param taskExecuteResponseCommand taskExecuteResponseCommand
     */
    void cacheTaskInstance(TaskExecuteResponseCommand taskExecuteResponseCommand);

    /**
     * remove taskInstance by taskInstanceId
     * @param taskInstanceId taskInstanceId
     */
    void removeByTaskInstanceId(Integer taskInstanceId);
}

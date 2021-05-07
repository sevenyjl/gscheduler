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
package com.gs.cd.gscheduler.api.service;

import static com.gs.cd.gscheduler.common.utils.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.gs.cd.gscheduler.api.enums.Status;
import com.gs.cd.gscheduler.api.utils.ZookeeperMonitor;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.enums.ZKNodeType;
import com.gs.cd.gscheduler.common.model.Server;
import com.gs.cd.gscheduler.common.model.WorkerServerModel;
import com.gs.cd.gscheduler.dao.MonitorDBDao;
import com.gs.cd.gscheduler.dao.entity.MonitorRecord;
import com.gs.cd.gscheduler.dao.entity.ZookeeperRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.collect.Sets;

/**
 * monitor service
 */
@Service
public class MonitorService extends BaseService {

  @Autowired
  private ZookeeperMonitor zookeeperMonitor;

  @Autowired
  private MonitorDBDao monitorDBDao;
  /**
   * query database state
   *
   * @param loginUser login user
   * @return data base state
   */
  public Map<String,Object> queryDatabaseState() {
    Map<String, Object> result = new HashMap<>(5);

    List<MonitorRecord> monitorRecordList = monitorDBDao.queryDatabaseState();

    result.put(Constants.DATA_LIST, monitorRecordList);
    putMsg(result, Status.SUCCESS);

    return result;

  }

  /**
   * query master list
   *
   * @return master information list
   */
  public Map<String,Object> queryMaster() {

    Map<String, Object> result = new HashMap<>(5);

    List<Server> masterServers = getServerListFromZK(true);
    result.put(Constants.DATA_LIST, masterServers);
    putMsg(result,Status.SUCCESS);

    return result;
  }

  /**
   * query zookeeper state
   *
   * @return zookeeper information list
   */
  public Map<String,Object> queryZookeeperState() {
    Map<String, Object> result = new HashMap<>(5);

    List<ZookeeperRecord> zookeeperRecordList = zookeeperMonitor.zookeeperInfoList();

    result.put(Constants.DATA_LIST, zookeeperRecordList);
    putMsg(result, Status.SUCCESS);

    return result;

  }


  /**
   * query worker list
   *
   * @param loginUser login user
   * @return worker information list
   */
  public Map<String,Object> queryWorker() {

    Map<String, Object> result = new HashMap<>(5);
    List<WorkerServerModel> workerServers = getServerListFromZK(false)
            .stream()
            .map((Server server) -> {
              WorkerServerModel model = new WorkerServerModel();
              model.setId(server.getId());
              model.setHost(server.getHost());
              model.setPort(server.getPort());
              model.setZkDirectories(Sets.newHashSet(server.getZkDirectory()));
              model.setResInfo(server.getResInfo());
              model.setCreateTime(server.getCreateTime());
              model.setLastHeartbeatTime(server.getLastHeartbeatTime());
              return model;
            })
            .collect(Collectors.toList());

    Map<String, WorkerServerModel> workerHostPortServerMapping = workerServers
            .stream()
            .collect(Collectors.toMap(
                    (WorkerServerModel worker) -> {
                        String[] s = worker.getZkDirectories().iterator().next().split("/");
                        return s[s.length - 1];
                    }
                    , Function.identity()
                    , (WorkerServerModel oldOne, WorkerServerModel newOne) -> {
                      oldOne.getZkDirectories().addAll(newOne.getZkDirectories());
                      return oldOne;
                    }));

    result.put(Constants.DATA_LIST, workerHostPortServerMapping.values());
    putMsg(result,Status.SUCCESS);

    return result;
  }

  public List<Server> getServerListFromZK(boolean isMaster) {

    checkNotNull(zookeeperMonitor);
    ZKNodeType zkNodeType = isMaster ? ZKNodeType.MASTER : ZKNodeType.WORKER;
    return zookeeperMonitor.getServersList(zkNodeType);
  }

}

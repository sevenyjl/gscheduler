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

import com.gs.cd.cloud.utils.jwt.JwtUserInfo;
import com.gs.cd.gscheduler.api.enums.Status;
import com.gs.cd.gscheduler.api.utils.PageInfo;
import com.gs.cd.gscheduler.api.utils.Result;
import com.gs.cd.gscheduler.common.Constants;
import com.gs.cd.gscheduler.common.utils.CollectionUtils;
import com.gs.cd.gscheduler.dao.entity.Queue;
import com.gs.cd.gscheduler.dao.mapper.QueueMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * queue service
 */
@Service
public class QueueService extends BaseService {

    private static final Logger logger = LoggerFactory.getLogger(QueueService.class);

    @Autowired
    private QueueMapper queueMapper;


    /**
     * query queue list
     *
     * @param loginUser login JwtUserInfo
     * @return queue list
     */
    public Map<String, Object> queryList(JwtUserInfo loginUser) {
        Map<String, Object> result = new HashMap<>(5);
        if (checkAdmin(loginUser, result)) {
            return result;
        }

        List<Queue> queueList = queueMapper.selectList(null);
        result.put(Constants.DATA_LIST, queueList);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * query queue list paging
     *
     * @param loginUser login JwtUserInfo
     * @param pageNo    page number
     * @param searchVal search value
     * @param pageSize  page size
     * @return queue list
     */
    public Map<String, Object> queryList(JwtUserInfo loginUser, String searchVal, Integer pageNo, Integer pageSize) {
        Map<String, Object> result = new HashMap<>(5);
        if (checkAdmin(loginUser, result)) {
            return result;
        }

        Page<Queue> page = new Page(pageNo, pageSize);


        IPage<Queue> queueList = queueMapper.queryQueuePaging(page, searchVal);

        Integer count = (int) queueList.getTotal();
        PageInfo<Queue> pageInfo = new PageInfo<>(pageNo, pageSize);
        pageInfo.setTotalCount(count);
        pageInfo.setLists(queueList.getRecords());
        result.put(Constants.DATA_LIST, pageInfo);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * create queue
     *
     * @param loginUser login JwtUserInfo
     * @param queue     queue
     * @param queueName queue name
     * @return create result
     */
    public Map<String, Object> createQueue(JwtUserInfo loginUser, String queue, String queueName) {
        Map<String, Object> result = new HashMap<>(5);
        if (checkAdmin(loginUser, result)) {
            return result;
        }

        if (StringUtils.isEmpty(queue)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "queue");
            return result;
        }

        if (StringUtils.isEmpty(queueName)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "queueName");
            return result;
        }

        if (checkQueueNameExist(queueName)) {
            putMsg(result, Status.QUEUE_NAME_EXIST, queueName);
            return result;
        }

        if (checkQueueExist(queue)) {
            putMsg(result, Status.QUEUE_VALUE_EXIST, queue);
            return result;
        }

        Queue queueObj = new Queue();
        Date now = new Date();

        queueObj.setQueue(queue);
        queueObj.setQueueName(queueName);
        queueObj.setCreateTime(now);
        queueObj.setUpdateTime(now);

        queueMapper.insert(queueObj);
        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * update queue
     *
     * @param loginUser login JwtUserInfo
     * @param queue     queue
     * @param id        queue id
     * @param queueName queue name
     * @return update result code
     */
    public Map<String, Object> updateQueue(JwtUserInfo loginUser, int id, String queue, String queueName) {
        Map<String, Object> result = new HashMap<>(5);
        if (checkAdmin(loginUser, result)) {
            return result;
        }

        if (StringUtils.isEmpty(queue)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "queue");
            return result;
        }

        if (StringUtils.isEmpty(queueName)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "queueName");
            return result;
        }

        Queue queueObj = queueMapper.selectById(id);
        if (queueObj == null) {
            putMsg(result, Status.QUEUE_NOT_EXIST, id);
            return result;
        }

        // whether queue value or queueName is changed
        if (queue.equals(queueObj.getQueue()) && queueName.equals(queueObj.getQueueName())) {
            putMsg(result, Status.NEED_NOT_UPDATE_QUEUE);
            return result;
        }

        // check queue name is exist
        if (!queueName.equals(queueObj.getQueueName())
                && checkQueueNameExist(queueName)) {
            putMsg(result, Status.QUEUE_NAME_EXIST, queueName);
            return result;
        }

        // check queue value is exist
        if (!queue.equals(queueObj.getQueue()) && checkQueueExist(queue)) {
            putMsg(result, Status.QUEUE_VALUE_EXIST, queue);
            return result;
        }

//        // check old queue using by any JwtUserInfo
//        if (checkIfQueueIsInUsing(queueObj.getQueueName(), queueName)) {
//            //update JwtUserInfo related old queue
//            Integer relatedUserNums = userMapper.updateUserQueue(queueObj.getQueueName(), queueName);
//            logger.info("old queue have related {} JwtUserInfo, exec update JwtUserInfo success.", relatedUserNums);
//        }

        // update queue
        Date now = new Date();
        queueObj.setQueue(queue);
        queueObj.setQueueName(queueName);
        queueObj.setUpdateTime(now);

        queueMapper.updateById(queueObj);

        putMsg(result, Status.SUCCESS);

        return result;
    }

    /**
     * verify queue and queueName
     *
     * @param queue     queue
     * @param queueName queue name
     * @return true if the queue name not exists, otherwise return false
     */
    public Result verifyQueue(String queue, String queueName) {
        Result result = new Result();

        if (StringUtils.isEmpty(queue)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "queue");
            return result;
        }

        if (StringUtils.isEmpty(queueName)) {
            putMsg(result, Status.REQUEST_PARAMS_NOT_VALID_ERROR, "queueName");
            return result;
        }


        if (checkQueueNameExist(queueName)) {
            logger.error("queue name {} has exist, can't create again.", queueName);
            putMsg(result, Status.QUEUE_NAME_EXIST, queueName);
            return result;
        }

        if (checkQueueExist(queue)) {
            logger.error("queue value {} has exist, can't create again.", queue);
            putMsg(result, Status.QUEUE_VALUE_EXIST, queue);
            return result;
        }

        putMsg(result, Status.SUCCESS);
        return result;
    }

    /**
     * check queue exist
     * if exists return true，not exists return false
     * check queue exist
     *
     * @param queue queue
     * @return true if the queue not exists, otherwise return false
     */
    private boolean checkQueueExist(String queue) {
        return CollectionUtils.isNotEmpty(queueMapper.queryAllQueueList(queue, null));
    }

    /**
     * check queue name exist
     * if exists return true，not exists return false
     *
     * @param queueName queue name
     * @return true if the queue name not exists, otherwise return false
     */
    private boolean checkQueueNameExist(String queueName) {
        return CollectionUtils.isNotEmpty(queueMapper.queryAllQueueList(null, queueName));
    }

    /**
     * check old queue name using by any JwtUserInfo
     * if need to update JwtUserInfo
     *
     * @param oldQueue old queue name
     * @param newQueue new queue name
     * @return true if need to update JwtUserInfo
     */
//    private boolean checkIfQueueIsInUsing (String oldQueue, String newQueue) {
//        return !oldQueue.equals(newQueue) && CollectionUtils.isNotEmpty(userMapper.queryUserListByQueue(oldQueue));
//    }

}
